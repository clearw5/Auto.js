import re

def get_functions(path):
	list = []
	with open(path) as f:
		for line in f:
			func = get_function_or_null(line)
			if func:
				list.append(func)
	return to_str(list)
	
def to_str(list):
	return str(list).replace("'", '"')

def get_function_or_null(line):
	m = re.match(r'^var\s(\S+)\s=', line)
	if m and not m.group(1).startswith('__'):
		return m.group(1)
	else:
		return None
		
if __name__ == "__main__":
	print(get_functions(r'autojs\src\main\assets\javascript_engine_init.js'))
	print(get_functions(r'app\src\main\assets\js\autojs_init.js'))
		