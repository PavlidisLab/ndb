import urllib2
import os
import stat

urls = ["http://hgdownload.soe.ucsc.edu/admin/exe/linux.x86_64/twoBitToFa",
        "http://hgdownload.cse.ucsc.edu/goldenPath/hg19/bigZips/hg19.2bit"]

for url in urls:
    #Thanks to PabloG @ stackoverflow
    #http://stackoverflow.com/questions/22676/how-do-i-download-a-file-over-http-using-python
    
    file_name = url.split('/')[-1]
    u = urllib2.urlopen(url)
    f = open(file_name, 'wb')
    meta = u.info()
    file_size = int(meta.getheaders("Content-Length")[0])
    print "Downloading: %s Bytes: %s" % (file_name, file_size)

    file_size_dl = 0
    block_sz = 8192
    while True:
        buffer = u.read(block_sz)
        if not buffer:
            break

        file_size_dl += len(buffer)
        f.write(buffer)
        status = r"%10d  [%3.2f%%]" % (file_size_dl, file_size_dl * 100. / file_size)
        status = status + chr(8)*(len(status)+1)
        print status,

    f.close()

print "Finished downloading all files."

st = os.stat('./twoBitToFa')
os.chmod('./twoBitToFa', st.st_mode | stat.S_IEXEC)

print "+x added to twoBitToFa."
