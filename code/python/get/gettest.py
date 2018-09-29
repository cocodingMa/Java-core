from urllib import parse,request

import hashlib
import requests
import json
import xlrd

# 将log写入文件
def record_log(log):
    error_file = open('code.txt', 'a')
    error_file.write(str(log) + "\n")
    error_file.close()
    print(str(log))

# 解析文件
def load_data():
    xls_file = xlrd.open_workbook("codetext.xlsx")
    xls_sheet = xls_file.sheets()[0]    # 第一个sheet
    col_value = xls_sheet.col_values(1) #第2列
    return col_value

# 请求参数
def param(tId):
    textmod={'operatorId':'','productid':'','t_id':tId}
    textmod = parse.urlencode(textmod)
    hl = hashlib.md5()
    hl.update((textmod+'').encode(encoding='utf-8'))
    encrypt = hl.hexdigest()
    textmod = {'operatorId': '', 'productid': '', 't_id': tId, 'encrypt': encrypt}
    #print(textmod)
    return textmod

# 发送请求
def res(depositId):
    url=''
    response = requests.get(url, params=param(depositId))
    #print(response.url)
    if response.status_code == 200:
        try:
            content = response.content
            content_json = json.loads(content)
            content_json = json.loads(content_json['result'])
            record_log(depositId+','+content_json['innerid'])
        except Exception as e:
            return None
    return None


def do_more():
    data = load_data()
    record_log(data)
    for i in data:
        res(str(i))

if __name__ == '__main__':

    print('start')
    do_more()
    print('end')