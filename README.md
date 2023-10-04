![nhlauncher](https://user-images.githubusercontent.com/111238843/236333484-35782200-224a-441d-9e8f-e2b54c86eeb1.png)

## What is NHLauncher?
NHLauncher is an application that makes it easier and faster to launch pentesting tools.

It uses NetHunter bridge for launching tools in nh terminal!

## Requirements 
- Android 6.0+
- Rooted android device (Its required for NetHunter installation)  
- Full NetHunter installation  
- New nethunter terminal and app (2023.1), you can download them from NetHunter store:
- Brain (working one)

## Main NHLauncher features
- Editable commands:
You can edit tool command by holding chosen button, and selecting appropriate option

- Adding buttons to 'Favourite Tools' category:  
You can add/remove button to favourites by holding chosen button, and clicking appropriate option. If theres any tools in favourites, NHLauncher will open this category by default

- Adding new button:  
You can your own button, by holding any button in chosen category, and clicking appropriate option

- Deleting button:  
You can delete button by holding it, and clicking appropriate option. Please note that you can only delete buttons that were created by you

- Threading:  
This app is running some tasks on threads, especially Sqlite queries, so you can operate without freezing your main ui thread

- Themes manager:  
You can fully customize NHLauncher appearance by selecting theme manager from top right toolbar

- Settings manager:  
You can customize NHLauncher settings, backup and restore your custom tools, favourites and own buttons, run missed setup by selecting settings manager from top right toolbar

- Sorting modes:  
You can easily select sorting mode for tools: Default, By usage, A-Z, Z-A, 0-9 A-Z, 9-0 Z-A

- Advanced searchbar:  
You can search for specific tools by their name using search icon in top left. It displays tools with first character in name same as searched one first, then displays tools that contains your search character anywhere!

- RecyclerView: 
Load a lot of buttons in second, and keep them in memory!

- Statistics: 
Check which tools you use the most with Statistics feature!

- Polish language: 
NHLauncher have Polish language!

- Availability: 
NHLauncher is available on older and newer android versions! 

- Dynamic themes: 
Dynamic themes support for A12+

## How to request new tool/s
If you like to request new tool for NHLauncher, first check all it functions on NetHunter platform.

Heres example of a request:  
(Second description is for Polish language!)
`insertTool(db, 1, "{CATEGORY}", 0, "{NAME}", "{DESCRIPTION}", "{DESCRIPTION}", "{COMMAND}", "{ICON}", 0);`  

`insertTool(db, 1, "01", 0, "nmap", "Network exploration tool and security / port scanner", "Network exploration tool and security / port scanner", "nmap -h", "kali_nmap", 0);`

**CATEGORY**:   
choose one of these...  
01 - Information Gathering  
02 - Vulnerability Analysis  
03 - Web Application Analysis  
04 - Database Assessment  
05 - Passwords Attacks  
06 - Wireless Attacks  
07 - Reverse Engineering  
08 - Exploitation Tools  
09 - Sniffing &amp; Spoofing  
10 - Post Exploitation  
11 - Forensics  
12 - Reporting Tools  
13 - Social Engineering  

**NAME**:  
Put tool name here...  

**DESCRIPTION:**  
Put tool description here, please make sure it's not longer than 90 characters

**COMMAND**
Put tool command here, display it help message by default, for example: nmap --help

**ICON:**
You can also attach tool image as svg, so i can scale it and put to tools database.
If you cant or dont want to add image, put `kali_menu` here  

**How to send request?**  
Open new issue with [TOOL REQUEST] tag in name, and ill check it...

## Features request
You can also send features request with [FEATURE REQUEST](https://github.com/cr4sh-me/NHLauncher/labels) label


