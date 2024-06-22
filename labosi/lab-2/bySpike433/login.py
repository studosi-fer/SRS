import DbManager
import sys
from getpass import getpass
from passlib.context import CryptContext
import re
# regex get from https://stackoverflow.com/questions/2990654/how-to-test-a-regex-password-in-python
# create CryptContext object
reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!#%*?&]{8,18}$"
mRegex = re.compile(reg)

# create CryptContext object
haser = CryptContext(schemes=["pbkdf2_sha256"], default="pbkdf2_sha256", pbkdf2_sha256__default_rounds=50000)


args = sys.argv
#args = ['.\\login.py', 'car']
username: str = args[1]
password = input("Enter password:\n")#getpass("Enter password:")
try:
    data : DbManager.Data = DbManager.searchByName(username)[0]

    if data.forced:
        if haser.verify(password, data.password):
            newPassword = input("Enter new password:\n")  # getpass("Enter new password:")
            pass2 = input("Repeat new password:\n")
            if newPassword == pass2:
                if bool(re.search(mRegex, newPassword)):
                    data.password = haser.hash(newPassword)
                    data.forced = False
                    DbManager.update(data)
                    print("Password changed successfully.")
                else:
                    print("Password must be 8 characters, uppercase letter, lowercase, number, and special (@#$%&+=)")
            else:
                print("Passwords do not match")
        else:
            print("Wrong username or password")
    else:
        if haser.verify(password, data.password):
            print("Login was successful.")
        else:
            print("Wrong username or password")

except:
    print("Wrong username or password")


