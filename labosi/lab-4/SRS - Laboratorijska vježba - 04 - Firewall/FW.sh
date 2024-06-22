#! /bin/sh
#
# Dodajte ili modificirajte pravila na oznacenim mjestima ili po potrebi (i želji) na 
# nekom drugom odgovarajucem mjestu (pazite: pravila se obrađuju slijedno!)
#
IPT=/sbin/iptables

$IPT -P INPUT DROP
$IPT -P OUTPUT DROP
$IPT -P FORWARD DROP

$IPT -F INPUT
$IPT -F OUTPUT
$IPT -F FORWARD

$IPT -A INPUT   -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A OUTPUT  -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT

#
# za potrebe testiranja dozvoljen je ICMP (ping i sve ostalo)
#
$IPT -A INPUT   -p icmp -j ACCEPT
$IPT -A FORWARD -p icmp -j ACCEPT
$IPT -A OUTPUT  -p icmp -j ACCEPT

#
# Primjer "anti spoofing" pravila na sucelju eth0
#
#$IPT -A INPUT   -i eth0 -s 127.0.0.0/8  -j DROP
#$IPT -A FORWARD -i eth0 -s 127.0.0.0/8  -j DROP
#$IPT -A INPUT   -i eth0 -s 198.51.100.0/24  -j DROP
#$IPT -A FORWARD -i eth0 -s 198.51.100.0/24  -j DROP
#$IPT -A INPUT   -i eth0 -s 10.0.0.0/24  -j DROP
#$IPT -A FORWARD -i eth0 -s 10.0.0.0/24  -j DROP


# DMZ pravila

# Web poslužitelju (tcp /80) i DNS poslužitelju (udp/53 i tcp/53) pokrenutima na www se može 
# pristupiti s bilo koje adrese (iz Interneta i iz lokalne mreže), ...
$IPT -A FORWARD -o eth2 -d 198.51.100.10 -p tcp --dport 80 -j ACCEPT
$IPT -A FORWARD -o eth2 -d 198.51.100.10 -p udp --dport 53 -j ACCEPT
$IPT -A FORWARD -o eth2 -d 198.51.100.10 -p tcp --dport 53 -j ACCEPT

# ... a SSH poslužitelju (na www) samo s racunala PC iz lokalne mreže (LAN)
$IPT -A FORWARD -s 10.0.0.20 -d 198.51.100.10 -p tcp --dport 22 -j ACCEPT

# S www je dozvoljen pristup poslužitelju database (LAN) na TCP portu 10000 te pristup 
# DNS poslužiteljima u Internetu (UDP i TCP port 53).
$IPT -A FORWARD -s 198.51.100.10 -d 10.0.0.100 -p tcp --dport 10000 -j ACCEPT
$IPT -A FORWARD -o eth2 -s 198.51.100.10 -p udp --dport 53 -j ACCEPT
$IPT -A FORWARD -o eth2 -s 198.51.100.10 -p tcp --dport 53 -j ACCEPT

# ... S www je zabranjen pristup svim ostalim adresama i poslužiteljima.
$IPT -A FORWARD -s 198.51.100.10 -j DROP

# Pristup svim ostalim adresama i poslužiteljima u DMZ je zabranjen.
$IPT -A FORWARD -o eth2 -j DROP


# LAN pravila

# Pristup SSH poslužitelju na cvoru database, koji se nalazi u lokalnoj mreži LAN, 
# dozvoljen je samo racunalima iz mreže LAN.
$IPT -A FORWARD -i eth1 -o eth1 -s 10.0.0.20/24 -d 10.0.0.100 -p tcp --dport 22 -j ACCEPT

# Web poslužitelju na cvoru database, koji sluša na TCP portu 10000, može se pristupiti
# iskljucivo s racunala www koje se nalazi u DMZ (i s racunala iz mreže LAN).
$IPT -A FORWARD -i eth2 -o eth1 -s 198.51.100.10 -d 10.0.0.100 -p tcp --dport 10000 -j ACCEPT
$IPT -A FORWARD -i eth1 -o eth1 -s 10.0.0.10/24 -d 10.0.0.100 -p tcp --dport 10000 -j ACCEPT

# S racunala database je zabranjen pristup svim uslugama u Internetu i u DMZ.
$IPT -A FORWARD -s 10.0.0.100 -j DROP

# Zabranjen je pristup svim ostalim uslugama na poslužitelju database (iz Interneta i iz DMZ)
$IPT -A FORWARD -d 10.0.0.100 -j DROP

# S racunala iz lokalne mreže (osim s database) se može pristupati svim racunalima u Internetu 
# ali samo korištenjem protokola HTTP (tcp/80) i DNS (udp/53 i tcp/53).
$IPT -A FORWARD ! -s 10.0.0.100 -i eth1 -o eth0 -p tcp --dport 80 -j ACCEPT
$IPT -A FORWARD ! -s 10.0.0.100 -i eth1 -o eth0 -p udp --dport 53 -j ACCEPT
$IPT -A FORWARD ! -s 10.0.0.100 -i eth1 -o eth0 -p tcp --dport 53 -j ACCEPT

# Pristup iz vanjske mreže u lokalnu LAN mrežu je zabranjen.
$IPT -A FORWARD -i eth0 -o eth1 -j DROP


# FW pravila

# Na FW je pokrenut SSH poslužitelj kojem se može pristupiti samo iz lokalne mreže i to samo sa cvora PC.
$IPT -A INPUT -i eth1 -s 10.0.0.20 -p tcp --dport 22 -j ACCEPT

# Pristup svim ostalim uslugama (portovima) na cvoru FW je zabranjen.
$IPT -A INPUT -p tcp --dport 22 -j DROP
$IPT -A INPUT -p tcp --dport 53 -j DROP
$IPT -A INPUT -p udp --dport 53 -j DROP
$IPT -A INPUT -p tcp --dport 80 -j DROP

# Internet pravila
$IPT -A FORWARD -d 203.0.113.10 -p tcp --dport 22 -j ACCEPT
$IPT -A FORWARD -d 203.0.113.10 -p tcp --dport 80 -j ACCEPT
