import os
import os.path
import sys

in_dir = r'..\api'
out_dir = r'..\docs'
json_out_dir = r'..\json'
template = r'..\template.html'
version = '4.1.0'


def process(in_file, out_file, format="html"):
    os.system(
        "node generate.js --template={0} --out={1} --node-version={2} {3} --format={4}".format(template, out_file, version, in_file, format))


def processAll():    
    for file in os.listdir(in_dir):
        if not file.endswith('.md'):
            continue
        name = os.path.splitext(file)[0]
        if name != 'all':
            processModule(name)
    processModule("all")
    index = os.path.join(out_dir, "index.html")        
    if os.path.exists(index):
        os.remove(index)
    os.rename(os.path.join(out_dir, "_toc.html"), index)


def processModule(module):
    process(os.path.join(in_dir, module + ".md"), os.path.join(out_dir, module + ".html"))
    process(os.path.join(in_dir, module + ".md"), os.path.join(json_out_dir, module + ".json"), "json")

if __name__ == '__main__':
    if len(sys.argv) > 1:
        process_all = True
        for module in sys.argv[1:]:
            processModule(module)
            if module == "all":
                process_all = False
        if process_all:
            processModule("all")
    else:
        processAll()




