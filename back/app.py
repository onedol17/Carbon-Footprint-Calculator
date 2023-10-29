from flask import Flask
from flask import request
from flask_cors import CORS
from flask import Response
import sys
import pandas as pd
import os
import json

app = Flask(__name__)
CORS(app)

@app.route('/register',methods=['POST'])
def register():
    data = request.get_json()
    print(data)
    d = {
        'userid': data['userID'],
        'password': data['password'],
        'spend': 0
    }

    new = pd.DataFrame([d])
    new.to_csv("db/user.csv", mode='a', header=False, index=False)
    print(new)
    return json.dumps({'status': 'success'})

@app.route('/login',methods=['POST'])
def login():
    data = request.get_json()
    df = pd.read_csv("db/user.csv")
    if data['userid'] in df['userid'].values.tolist():
        return json.dumps({'status': 'success'})
    return json.dumps({'status': 'failed'})


@app.route('/update', methods=['POST'])
def update():
    data = request.get_json()
    df = pd.read_csv("db/user.csv")
    t = 0
    ml = df['userid'].values.tolist()
    for i in range(1, len(ml)):
        if ml[i] == data['userid']:
            t = i
    df.loc[t] = [df.iloc[t]['userid'], df.iloc[t]['password'], data['spend']]
    df.to_csv("db/user.csv", mode='w', header=True, index=False)
    
    return json.dumps({'status': 'success'})


if __name__ == "__main__":
    app.run(host='0.0.0.0', port = 5000)