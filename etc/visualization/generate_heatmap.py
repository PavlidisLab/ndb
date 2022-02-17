#!/usr/bin/env python3

import argparse
from subprocess import run

import pandas as pd
import numpy as np

from bokeh.plotting import (figure,
                            output_file,
                            save)
from bokeh.models import (ColumnDataSource, LabelSet, TapTool,
                          CustomJS)
from bokeh.transform import dodge

parser = argparse.ArgumentParser()

parser.add_argument('data_file')
parser.add_argument('output_file')
args = parser.parse_args()

variant_data = pd.read_table(args.data_file)

# ### Prepare data for bokeh format
#
# Should have xname, yname, colors, alphas, count.

data = variant_data[["paper_key", "event_id"]]
data.set_index("paper_key", inplace=True)
data = data.reset_index()
data["event_id"] = pd.Categorical(data["event_id"])
data["paper_key"] = pd.Categorical(data["paper_key"])
data = data.drop_duplicates()

# Make a paper-paper_id mapping table
paper_id_table = variant_data[["paper_key", "pid"]].drop_duplicates()

# ### Compute overlaps
papers = data["paper_key"].drop_duplicates()
paper_overlap_list = [data.groupby("paper_key")["event_id"].aggregate(
    lambda x: x.isin(data.loc[data["paper_key"] == k, "event_id"]).sum()) for k
    in papers]

# Merge into a single dataframe
overlaps_df = pd.concat(paper_overlap_list, axis=1)
overlaps_df.columns = papers
overlaps_df = overlaps_df.loc[papers, papers]

# Unstack and make a 2-column molten dataframe.
HAS_OVERLAPS = np.diag(overlaps_df) != overlaps_df.sum()
PLT_SIZE = 1300
overlap_unstack = pd.DataFrame(
    overlaps_df.loc[HAS_OVERLAPS, HAS_OVERLAPS].stack())

overlap_unstack.index.names = ["xname", "yname"]
overlap_unstack = overlap_unstack.reset_index()
overlap_unstack = overlap_unstack[overlap_unstack[0] > 0]

paper_key_id_dict = dict(
    zip(paper_id_table["paper_key"], paper_id_table["pid"]))

URL_TEMPLATE = 'variant?paperId={pid_1}&overlapPaperId={pid_2}'

js_callback = """
    /* Essentially like OpenURL, but works around the encoding issues for the ampersand. */
    var indices = source.selected.indices;

    for (var i = 0; i < indices.length; i++) {
        var ind = indices[i];
        var url = source.data['url'][ind];
        window.open( url , '_blank');
    }

    source.selected.indices = []
"""

OVERLAP_COLOR = "dodgerblue"
DIAG_COLOR = "#FFD699"
NOTHING_COLOR = "white"

xname = overlap_unstack["xname"].tolist()
yname = overlap_unstack["yname"].tolist()
counts = overlap_unstack[0].tolist()

# Set colors
color = [OVERLAP_COLOR if x > 0 else NOTHING_COLOR for x in
         counts]  # Active or empty
color = [c if str(x) != str(y) else DIAG_COLOR for x, y, c in
         zip(xname, yname, color)]  # Paint diagonal.

# Set alpha of color
alpha = [min(x, 1.0) for x in np.log(overlap_unstack[0]) / 10]
alpha = [a if str(x) != str(y) else 1.0 for x, y, a in
         zip(xname, yname, alpha)]  # Set diagonal to 1.0 alpha

# Get a sorted list of names
uname = sorted(list(set(xname)))

# Create a Column Data Source
data = ColumnDataSource(dict(
    xname=xname,
    yname=yname,
    colors=color,
    alphas=alpha,
    count=counts,
    txtcount=[str(c) if len(str(c)) < 4 else str(c)[:-3] + "K" for c in counts],
    url=[URL_TEMPLATE.format(pid_1=paper_key_id_dict[k1],
                             pid_2=paper_key_id_dict[k2])
         for k1, k2 in zip(xname, yname)
         ]
))

# Make figure
p = figure(title="Variant Event Overlap Between Papers",
           x_axis_location="above",
           tools="hover,tap",
           x_range=list(reversed(uname)),
           y_range=uname,
           tooltips="""
           <div style="">
            <p  style='margin:0'> <strong>@xname</strong> reports  </p>
            <p  style='margin:0'> <strong>@count</strong> variant events also reported in </p>
            <p  style='margin:0'> <strong>@yname</strong>. </p>
           </div>
            """
           )
p.title.text_font_size = '18pt'  ## Title size

# Label for inside the cells of the heatmap
labels = LabelSet(x="xname",
                  y=dodge('yname', -0.25, range=p.y_range),
                  text='txtcount',
                  source=data,
                  text_align='center',
                  )
labels.text_font_size = "10px"
labels.text_font_style = "bold"

p.plot_width = PLT_SIZE
p.plot_height = PLT_SIZE
p.grid.grid_line_dash_offset = 7
p.grid.grid_line_color = None
p.axis.axis_line_color = None
# p.axis.major_tick_line_color = None # Enables lines though centered on 0.0 of the factor value; needs offset to make a heatmap grid.
p.axis.major_label_text_font_size = "12px"
p.axis.major_label_standoff = 10
p.xaxis.major_label_orientation = np.pi / 3

# Add heatmap tiles
rects = p.rect('xname',
               'yname',
               0.9, 0.9,
               source=data,
               color='colors',
               alpha='alphas',
               dilate=True,
               line_color='black',
               hover_line_color='black',
               hover_color='colors')

# Render labels
p.add_layout(labels)
p.toolbar.logo = None  # Remove Bokeh logo

# Add callback
taptool = p.select(type=TapTool)
taptool.callback = CustomJS(args=dict(source=data), code=js_callback)

# Produce file
output_file(args.output_file)  # Save to file.
save(p)  # Show the plot

# #### Fix a bug with the meta tag not having a closing slash
run(['sed', '-i', 's|\\(.*meta.*\\)>|\\1/>|g', args.output_file])

print(f'The heatmap has been exported to {args.output_file}.')
