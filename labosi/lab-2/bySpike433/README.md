# to run script enter in bash

./run.sh

# Security

I used pbkdf2_sha254 with 50000 rounds for hashing passwords with random salt.
Random salt ensures that same password will be generated diferently.

Slower than sha256, benefit when doing dictionary attack.

Used password regex for small, capital letters, numbers and special characters.