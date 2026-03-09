# Networking

A subnet is a smaller network that exists within a network

same IP address can be used on multiple, isolated networks while still allowing these to communicate with each other if configured correctly

IPv4 addresses are 32-bit addresses. Each byte, or 8-bit segment of the address, is divided by a period and typically expressed as a number 0–255 called octet.
192.168.0.5
11000000 - 10101000 - 00000000 - 00000101 (binary representation)

IPv6 expresses addresses as an 128-bit number

Simply put, the world now has too many internet-connected devices for the amount of addresses available through IPv4, so IPv6 is slowly replacing IPv4

IP addresses are typically made of two separate components. The first part of the address is used to identify the network and the part that comes afterwards is used to specify a specific host within that network. The amount of the address that each of these take up is dependent.

CIDR, was developed as an alternative to traditional subnetting. The idea is that you can add a specification in the IP address itself as the number of bits that make up the networking or routing portion

192.168.0.15/24: the first 24 bits of the IP address given are considered for the network

formula to do subnetting: https://dev.to/uyimeoku/understanding-subnetting-4idh