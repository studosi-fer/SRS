import DbManager
import sys
from getpass import getpass
from passlib.context import CryptContext
from DataTemplate import Data
import re
# regex get from https://stackoverflow.com/questions/2990654/how-to-test-a-regex-password-in-python
# create CryptContext object
reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!#%*?&]{8,18}$"
mRegex = re.compile(reg)

# create CryptContext object
haser = CryptContext(schemes=["pbkdf2_sha256"], default="pbkdf2_sha256", pbkdf2_sha256__default_rounds=50000)

DbManager.createOrDropTable()

#args = ['.\\usermgmt.py', 'add', 'car']
args = sys.argv
#print(DbManager.showData())

if args.__contains__("add"):
    username : str = args[2]
    password1 = input("Enter password:\n")#getpass("Enter password:")
    password2 = input("Repeat password:\n")#getpass("Repeat password:")

    if password1 == password2:
        if bool(re.search(mRegex, password1)):
            if DbManager.searchByName(username) == []:
                DbManager.insert(username, haser.hash(password1), False)
                print("A User added successfully.")
            else:
                print("Error occured, please try again.")
        else:
            print("Password must be 8 characters, uppercase letter, lowercase, number, and special (@#$%&+=)")
    else:
        print("A passwords do not match, user isn't added.")
elif args.__contains__("passwd"):
    username: str = args[2]
    password1 = input("Enter password:\n")#getpass("Enter password:")
    password2 = input("Repeat password:\n")#getpass("Repeat password:")

    if password1 == password2:
        dataList : list = DbManager.searchByName(username)
        if dataList != []:
            data : Data = dataList[0]
            data.password = haser.hash(password1)
            DbManager.update(data)
            print("Password changed successfuly.")
        else:
            print("Error occured, please try again.")
    else:
        print("A passwords do not match, the password isn't changed.")

elif args.__contains__("forcepass"):
    username: str = args[2]
    dataList: list = DbManager.searchByName(username)
    if dataList == []:
        print("Error occured, please try again.")
    else:
        data : DbManager.Data = dataList[0]
        data.forced = True
        DbManager.update(data)
        print("A User will be requested to change password on next login")

elif args.__contains__("del"):
    username: str = args[2]
    dataList: list = DbManager.searchByName(username)
    if dataList == []:
        print("Error occured, please try again.")
    else:
        data: DbManager.Data = dataList[0]
        DbManager.delete(data)
        print("User deleted successfully")