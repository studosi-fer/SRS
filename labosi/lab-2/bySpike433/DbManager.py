import sqlite3
from passlib.context import CryptContext
from DataTemplate import Data

# create CryptContext object
haser = CryptContext(schemes=["pbkdf2_sha256"], default="pbkdf2_sha256", pbkdf2_sha256__default_rounds=50000)



def createOrDropTable():
    con, cur = connectToDb()
    cur.execute("CREATE TABLE IF NOT EXISTS account(id INTEGER PRIMARY KEY, name TEXT, password TEXT,forced BOOL)")
    closeConnection(con)

def showData():
    con, cur = connectToDb()
    cur.execute("SELECT * FROM account")
    rows = cur.fetchall()
    con.close()
    return rows

def searchByName(name):
    con, cur = connectToDb()
    cur.execute("SELECT * FROM account WHERE name=?",(name,))
    rows = cur.fetchall()
    dataList : list =[]
    for row in rows:
        dataList.append(Data(row))
    con.close()
    return dataList
def insert(name, password, forced):
    con, cur = connectToDb()
    cur.execute("INSERT INTO account VALUES(NULL,?,?,?)",(name,password, forced))
    closeConnection(con)

def update(data : Data):
    con, cur = connectToDb()
    cur.execute("UPDATE account SET name=?,password=?,forced=? WHERE id=?",(data.name,data.password,data.forced,data.id))
    closeConnection(con)

def delete(data : Data):
    con, cur = connectToDb()
    id = data.id
    cur.execute("DELETE FROM account WHERE id=?",(id,))
    closeConnection(con)

def closeConnection(con):
    con.commit()
    con.close()

def connectToDb():
    con = sqlite3.connect("files.db")
    cur = con.cursor()
    return con, cur
