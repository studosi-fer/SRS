version=$(python -V 2>&1 | grep -Po '(?<=Python )(.+)')
if [[ -z "$version" ]]
then
    echo "Python not installed"
fi

echo "Python installed: [OK]"

pip install virtualenv

#create virtualenv
python -m virtualenv .

echo 
echo "Activate virtual enviroment"
echo ". bin/activate"
#activate it
. bin/activate


#install pacakges
pip3 install passlib

file="files.db"

if [ -f "$file" ] ; then
    rm "$file"
fi

echo "***********************************"

echo "Add user mile with differenet password"
echo -e "abcdABCD#1\nabcdABCD#2\n" | python3 usermgmt.py add mile
echo "***********************************"

echo "Add user mile with without spcecial char and with less than 8 chars"
echo -e "a\na\n" | python3 usermgmt.py add mile
echo "***********************************"


echo "Add user mile with same password"
echo -e "abcdABCD#1\nabcdABCD#1\n" | python3 usermgmt.py add mile
echo "***********************************"

echo "Add another user mileCar"
echo -e "abcdABCD#1\nabcdABCD#1\n" | python3 usermgmt.py add mileCar
echo "***********************************"

echo "Delete mile"
python3 usermgmt.py del mile
echo "***********************************"

echo "Delete mile again"
python3 usermgmt.py del mile
echo "***********************************"

echo "Change password for mileCar"
echo -e "abcdABCD#2\nabcdABCD#2\n" | python3 usermgmt.py passwd mileCar
echo "***********************************"

echo "Login mileCar with right password"
echo -e "abcdABCD#2\n" | python3 login.py mileCar
echo "***********************************"

echo "Login mileCar with wrong password"
echo -e "a\n" | python3 login.py mileCar
echo "***********************************"

echo "Force checkout for non existing mileCar2"
python3 usermgmt.py forcepass mileCar2
echo "***********************************"

echo "Force checkout for existing mileCar"
python3 usermgmt.py forcepass mileCar
echo "***********************************"

echo "MileCar is forced to change password at login"
echo -e "abcdABCD#2\nabcdABCD#1\nabcdABCD#1\n" | python3 login.py mileCar
echo "***********************************"

echo "Login with MileCar again with changed pasword"
echo -e "abcdABCD#1\n" | python3 login.py mileCar
echo "***********************************"

echo "Login with unexisting account mileCar2"
echo -e "abcdABCD#1\n" | python3 login.py mileCar2

echo "***********************************"
