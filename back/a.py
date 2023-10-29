import os
import pandas as pd

data_path = os.getcwd() + "\\db"
df = pd.read_csv(data_path + "\\user.csv")

t = 0
l = df['userid'].values.tolist()

for i in range(len(l)):
  if l[i] == "admin":
    t = i

spend = 1000

new = {'userid': df.iloc[t]['userid'], 'password': df.iloc[t]['password'],'spend': spend}

df.iloc[t] = new
df.to_csv(data_path + "\\user.csv", index=False)

print(df)