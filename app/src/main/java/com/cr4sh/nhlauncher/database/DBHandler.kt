package com.cr4sh.nhlauncher.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// This class creates database and all elements
class DBHandler(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // Initiate database, insert elements
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE TOOLS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "SYSTEM INT, "
                    + "CATEGORY TEXT, "
                    + "FAVOURITE INT, "
                    + "NAME TEXT, "
                    + "DESCRIPTION_EN TEXT, "
                    + "DESCRIPTION_PL TEXT, "
                    + "CMD TEXT, "
                    + "ICON TEXT, "
                    + "USAGE INT);"
        )

        // Category 1
        insertTool(
            db,
            1,
            "1",
            0,
            "p0f",
            "Identify remote systems passively",
            "Pasywna identyfikacja systemów zdalnych",
            "p0f -h",
            "kali_p0f",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "spiderfoot",
            "It automates OSINT for threat intelligence and mapping your attack surface",
            "Automatyzuje OSINT w celu analizy zagrożeń i mapowania powierzchni ataku",
            "spiderfoot --help",
            "kali_spiderfoot",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dmitry",
            "Deepmagic Information Gathering Tool",
            "Magiczne narzędzie do zbierania informacji",
            "dmitry",
            "kali_dmitry",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "netdiscover",
            "Active/passive ARP reconnaissance tool",
            "Aktywne/pasywne narzędzie do rozpoznania ARP",
            "netdiscover -i wlan0",
            "kali_netdiscover",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "nmap",
            "Network exploration tool and security / port scanner",
            "Narzędzie do eksploracji sieci i skaner bezpieczeństwa / portów",
            "nmap -h",
            "kali_nmap",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "ike-scan",
            "Discover and fingerprint IKE hosts (IPsec VPN servers)",
            "Wykrywanie i odcisk palca hostów IKE (serwery IPsec VPN)",
            "ike-scan -h",
            "kali_ike_scan",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "recon-ng",
            "Web Reconnaissance framework",
            "Struktura rozpoznania sieci",
            "recon-ng",
            "kali_recon_ng",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "OWASP Amass",
            "In-depth Attack Surface Mapping and Asset Discovery",
            "Dogłębne mapowanie obszaru ataku i wykrywanie zasobów",
            "amass -h",
            "owasp",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "OWASP Maryam",
            "Advanced OSINT Framework",
            "Zaawansowane środowisko OSINT",
            "maryam -h",
            "owasp",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dnsenum",
            "Enumerates DNS information of a domain and to discover non-contiguous ip blocks",
            "Wylicza informacje DNS domeny i odkrywa nieciągłe bloki adresów IP",
            "dnsenum -h",
            "kali_dnsenum",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dnsmap",
            "Scan for subdomains using bruteforcing techniques",
            "Skanuj w poszukiwaniu subdomen przy użyciu technik bruteforce",
            "dnsmap -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dnsrecon",
            "DNS Enumeration Script",
            "Skrypt wyliczania DNS",
            "dnsrecon -h",
            "kali_dnsrecon",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dnstracer",
            "Automated checker of delegates subdomains",
            "Automatyczne sprawdzanie subdomen delegatów",
            "dnstracer -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "dnswalk",
            "Attempt to get DNS zone information from the target domain",
            "Próba uzyskania informacji o strefie DNS z domeny docelowej",
            "dnswalk -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "fierce",
            "A semi-lightweight scanner that helps locate non-contiguous IP space",
            "Skaner, który pomaga zlokalizować nieciągłą przestrzeń IP",
            "fierce -h",
            "kali_fierce",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "urlcrazy",
            "Generate and test domain typos and variations to detect and perform typo squatting",
            "Generuj i testuj literówki i odmiany domeny, aby wykrywać i wykonywać przysiady literowe",
            "urlcrazy -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "firewalk",
            "Determine what layer 4 protocols a given IP forwarding device will pass",
            "Określ, jakie protokoły warstwy 4 przejmie dane urządzenie przekazujące IP",
            "firewalk -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "fragrouter",
            "Fragrouter is a network intrusion detection evasion toolkit",
            "Fragrouter to zestaw narzędzi do wykrywania włamań sieciowych służących do unikania ataków",
            "fragrouter --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "ftest",
            "A tool designed for testing firewall filtering policies and Intrusion Detection System (IDS) capabilities",
            "Narzędzie przeznaczone do testowania zasad filtrowania zapory i możliwości systemu wykrywania włamań (IDS)",
            "ftest --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "lbd",
            "Test to see if the target domain is using a load balancer",
            "Sprawdź, czy domena docelowa korzysta z systemu równoważenia obciążenia",
            "lbd",
            "kali_lbd",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "wafw00f",
            "This package identifies and fingerprints Web Application Firewall (WAF) products",
            "Ten pakiet identyfikuje i odciski palców produktów Web Application Firewall (WAF)",
            "wafw00f -h",
            "kali_wafw00f",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "arping",
            "The arping utility sends ARP and/or ICMP requests to the specified host and displays the replies",
            "Narzędzie arping wysyła żądania ARP i/lub ICMP do określonego hosta i wyświetla odpowiedzi",
            "arping --help",
            "kali_arping",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "fping",
            "Send ICMP echo request to determine if a target host is responding",
            "Wyślij żądanie echa ICMP, aby określić, czy host docelowy odpowiada",
            "fping -h",
            "kali_fping",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "hping3",
            "A tool able to send custom ICMP/UDP/TCP packets and to display target replies",
            "Narzędzie zdolne do wysyłania niestandardowych pakietów ICMP/UDP/TCP i wyświetlania docelowych odpowiedzi",
            "hping3 -h",
            "kali_hping3",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "masscan",
            "A fast TCP port scanner which transmits SYN packets asynchronously and produces results",
            "Szybki skaner portów TCP, który asynchronicznie przesyła pakiety SYN i generuje wyniki",
            "masscan --help",
            "kali_masscan",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "ncat",
            "Reimplementation of Netcat by the NMAP project, concatenate and redirect sockets",
            "Reimplementacja Netcat przez projekt NMAP, łączenie i przekierowanie gniazd",
            "ncat -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "thcping6",
            "This tool sends a hand crafted ping6 packet",
            "To narzędzie wysyła ręcznie spreparowany pakiet ping6",
            "atk6-thcping6 -h",
            "kali_thcping6",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "unicornscan",
            "A new information gathering and correlation engine",
            "Nowy silnik do zbierania i korelacji informacji",
            "unicornscan -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "theharvester",
            "It is used to gather open source intelligence (OSINT) on a company or domain",
            "Służy do zbierania danych wywiadowczych typu open source (OSINT) dotyczących firmy lub domeny",
            "theHarvester --help",
            "kali_theharvester",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "twofi",
            "Generate word lists from twitter searches",
            "Generuj listy słów z wyszukiwań na Twitterze",
            "twofi --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "0trace",
            "Bypassing some types of stateful packet filters with ease",
            "Łatwe omijanie niektórych typów stanowych filtrów pakietów",
            "0trace.sh",
            "kali_0trace",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "intrace",
            "A traceroute-like application that enables users to enumerate IP hops exploiting existing TCP connections",
            "Aplikacja podobna do traceroute, która umożliwia użytkownikom wyliczanie przeskoków IP wykorzystujących istniejące połączenia TCP",
            "intrace -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "irpas-ass",
            "Autonomous system scanner",
            "Autonomiczny skaner systemu",
            "ass",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "irpass-cdp",
            "Cisco discovery protocol packet sender",
            "Nadawca pakietu protokołu wykrywania Cisco",
            "cdp",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "netmask",
            "An address netmask generation utility",
            "Narzędzie do generowania maski sieci adresu",
            "netmask --help",
            "kali_netmask",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "enum4linux",
            "Enum4linux is a tool for enumerating information from Windows and Samba systems",
            "Enum4linux to narzędzie do wyliczania informacji z systemów Windows i Samba",
            "enum4linux -h",
            "kali_enum4linux",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "nbtscan",
            "NBTscan is a program for scanning IP networks for NetBIOS name information",
            "NBTscan to program do skanowania sieci IP w poszukiwaniu informacji o nazwach NetBIOS",
            "nbtscan --help",
            "kali_nbtscan",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "smbmap",
            "SMBMap allows users to enumerate samba share drives across an entire domain",
            "SMBMap umożliwia użytkownikom wyliczanie dysków udostępnianych samby w całej domenie",
            "smbmap -h",
            "kali_smbmap",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "smtp-user-enum",
            "Username guessing tool primarily for use against the default Solaris SMTP service",
            "Narzędzie do odgadywania nazwy użytkownika, głównie do użytku z domyślną usługą Solaris SMTP",
            "smpt-user-enum",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "swaks",
            "Swiss Army Knife SMTP is a command-line tool written in Perl for testing SMTP setups",
            "Swiss Army Knife SMTP to narzędzie wiersza poleceń napisane w języku Perl do testowania konfiguracji SMTP",
            "swaks --help",
            "kali_swaks",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "braa",
            "Braa is a mass snmp scanner",
            "Braa to masowy skaner snmp",
            "braa -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "onesixtyone",
            "Onesixtyone is a simple SNMP scanner which sends SNMP requests for the sysDescr value",
            "Onesixtyone to prosty skaner SNMP, który wysyła żądania SNMP dla wartości sysDescr",
            "onesixtyone -h",
            "kali_onesixtyone",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "snmp-check",
            "Enumerates the SNMP devices and places the output in a very human readable friendly format",
            "Wylicza urządzenia SNMP i umieszcza dane wyjściowe w bardzo czytelnym dla człowieka formacie",
            "snmp-check -h",
            "kali_snmp_check",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "ssldump",
            "ffic on a network and analyze it for SSLv3/TLS network traffic",
            "działaj w sieci i analizuj ją pod kątem ruchu sieciowego SSLv3/TLS",
            "ssldump -h",
            "kali_ssldump",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "sslscan",
            "SSLScan queries SSL services, such as HTTPS, in order to determine the ciphers that are supported",
            "SSLScan wysyła zapytania do usług SSL, takich jak HTTPS, w celu określenia obsługiwanych szyfrów",
            "sslscan -h",
            "kali_sslscan",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "sslyze",
            "SSLyze is a Python tool that can analyze the SSL configuration of a server by connecting to it",
            "SSLyze to narzędzie Pythona, które może analizować konfigurację SSL serwera, łącząc się z nim",
            "sslyze -h",
            "kali_sslyze",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "tlssled",
            "A script whose purpose is to evaluate the security of a target SSL/TLS (HTTPS) web server implementation",
            "Jego celem jest ocena bezpieczeństwa docelowej implementacji serwera sieciowego SSL/TLS (HTTPS)",
            "tlssled -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "WHOIS",
            "DOMAIN/IP information tool",
            "Informacje o domenie/IP",
            "whois -h",
            "whois",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "whatweb",
            "Next generation web scanner",
            "Nowoczesny skaner stron internetowych",
            "whatweb -h",
            "kali_whatweb",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "nping",
            "Universal network packet generator",
            "Uniwersalny generator pakietów sieciowych",
            "nping -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "CloudFail",
            "Utilize misconfigured DNS and old database records to find hidden IP's behind the CloudFlare network",
            "Wykorzystaj źle skonfigurowany DNS i stare rekordy bazy danych, aby znaleźć ukryte adresy IP za siecią CloudFlare",
            "cd /root/CloudFail && python3 cloudfail.py -h",
            "cloudflare",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "CloudMare",
            "Automatic CloudProxy and Reverse Proxy bypass tool",
            "Automatyczne narzędzie obejścia CloudProxy i Reverse Proxy",
            "cd /root/Cloudmare && python3 Cloudmare.py -h",
            "cloudflare",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "Sublist3r",
            "Python tool designed to enumerate subdomains of websites using OSINT",
            "Narzędzie pythona przeznaczone do wyliczania subdomen stron internetowych wykorzystujących OSINT",
            "cd /root/Sublist3r && python3 sublist3r.py -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "1",
            0,
            "PWNedOrNot",
            "OSINT tool for finding passwords of compromised email addresses ",
            "Narzędzie OSINT do wyszukiwania haseł do zaatakowanych adresów e-mail",
            "cd /root/pwnedOrNot && python3 pwnedornot.py -h",
            "pwned",
            0
        )
        // Category 2
        insertTool(
            db,
            1,
            "2",
            0,
            "lynis",
            "Security auditing tool for Linux, macOS, and UNIX-based systems",
            "Narzędzie do audytu bezpieczeństwa dla systemów Linux, macOS i UNIX",
            "lynis -h",
            "kali_lynis",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "peass",
            "Privilege Escalation Awesome Scripts SUITE",
            "Pakiet niesamowitych skryptów do eskalacji uprawnień",
            "peass",
            "kali_peass",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "afl",
            "american fuzzy lop - a security-oriented fuzzer ",
            "american fuzzy lop – fuzzer zorientowany na bezpieczeństwo",
            "afl-fuzz -h",
            "kali_afl",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "unix-privesc-check",
            "Shell script to check for simple privilege escalation vectors on Unix systems",
            "Skrypt powłoki do sprawdzania prostych wektorów eskalacji uprawnień w systemach Unix",
            "unix-privesc-check",
            "kali_unix_privesc_check",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "nikto",
            "Nikto web server scanner ",
            "Skaner serwera WWW Nikto",
            "nikto -h",
            "kali_nikto",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "bed",
            "BED is a program which is designed to check daemons for potential buffer overflows, format strings et. al",
            "Sprawdzanie demonów pod kątem potencjalnych przepełnień bufora, formatowania ciągów znaków i innych",
            "bed",
            "kali_bed",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "wapiti",
            "A web application vulnerability scanner in Python",
            "Skaner podatności aplikacji internetowych w Pythonie",
            "wapiti -h",
            "kali_wapiti",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "wapiti-getcookie",
            "A Wapiti utility to fetch cookies from a webpage and store them in the Wapiti JSON format",
            "Narzędzie Wapiti do pobierania plików cookie ze strony internetowej i przechowywania ich w formacie Wapiti JSON",
            "wapiti-getcookie -h",
            "kali_wapiti",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "gvm",
            "Run The Greenbone Vulnerability Manager on localhost",
            "Uruchom program Greenbone Vulnerability Manager na hoście lokalnym",
            "gvm-start --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "cisco-auditing-tool",
            "Perl script which scans cisco routers for common vulnerabilities",
            "Skrypt Perla, który skanuje routery Cisco w poszukiwaniu typowych luk w zabezpieczeniach",
            "CAT -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "cisco-global-exploiter",
            "Cisco Global Exploiter (CGE), is an advanced, simple and fast security testing tool",
            "Cisco Global Exploiter (CGE) to zaawansowane, proste i szybkie narzędzie do testowania bezpieczeństwa",
            "cge.pl -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "cisco-ocs",
            "Attempt to exploit Cisco devices in the given IP range ",
            "Próba wykorzystania urządzeń Cisco w podanym zakresie IP",
            "cisco-ocs -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "cisco-torch",
            "mass scanning, fingerprinting, and exploitation tool",
            "masowe skanowanie, pobieranie odcisków palców i narzędzie do wykorzystywania",
            "cisco-torch -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "copy-router-config",
            "This package copies configuration files from Cisco devices running SNMP",
            "Ten pakiet kopiuje pliki konfiguracyjne z urządzeń Cisco z uruchomionym SNMP",
            "copy-router-config.pl -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "merge-router-config",
            "This package merges configuration files from Cisco devices running SNMP",
            "Ten pakiet łączy pliki konfiguracyjne z urządzeń Cisco z uruchomionym SNMP",
            "merge-router-config.pl -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "ohrwurm",
            "ohrwurm is a small and simple RTP fuzzer that has been successfully tested on a small number of SIP phones",
            "ohrwurm to mały i prosty fuzzer RTP, który został pomyślnie przetestowany na niewielkiej liczbie telefonów SIP",
            "ohrwurm -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "sfuzz",
            "sfuzz is a really simple to use black box testing suite called Simple Fuzzer",
            "sfuzz to naprawdę prosty w użyciu pakiet do testowania czarnej skrzynki o nazwie Simple Fuzzer",
            "sfuzz -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "siparmyknife",
            "fuzzer that searches for cross site scripting, SQL injection, log injection, and more",
            "fuzzer, który wyszukuje cross-site scripting, wstrzyknięcie SQL, wstrzyknięcie dziennika i więcej",
            "siparmyknife --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "generic_chunked",
            "Analyze a new network protocol for buffer overflows or similar weaknesses",
            "Analizuj nowy protokół sieciowy pod kątem przepełnienia bufora lub podobnych słabości",
            "generic_chunked -h",
            "kali_spike",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "generic_listen_tcp",
            "Analyze a new network protocol for buffer overflows or similar weaknesses",
            "Analizuj nowy protokół sieciowy pod kątem przepełnienia bufora lub podobnych słabości",
            "generic_listen_tcp -h",
            "kali_spike",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "generic_send_tcp",
            "Analyze a new network protocol for buffer overflows or similar weaknesses",
            "Analizuj nowy protokół sieciowy pod kątem przepełnienia bufora lub podobnych słabości",
            "generic_send_tcp -h",
            "kali_spike",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "generic_send_udp",
            "Analyze a new network protocol for buffer overflows or similar weaknesses",
            "Analizuj nowy protokół sieciowy pod kątem przepełnienia bufora lub podobnych słabości",
            "generic_send_udp -h",
            "kali_spike",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "dhcpig",
            "DHCPig initiates an advanced DHCP exhaustion attack",
            "DHCPig inicjuje zaawansowany atak wyczerpania DHCP",
            "dhcpig -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "iaxflood",
            "Flood the VoIP server by sending packets",
            "Zalej serwer VoIP wysyłając pakiety",
            "iaxflood -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "inviteflood",
            "A tool to perform SIP/SDP INVITE message flooding over UDP/IP",
            "Narzędzie do wykonywania zalewania komunikatów SIP/SDP INVITE przez UDP/IP",
            "inviteflood -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "siege",
            "Siege is an regression test and benchmark utility",
            "Siege to narzędzie do testów regresji i testów porównawczych",
            "siege -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "slowhttptest",
            "A highly configurable tool that simulates some application layer Denial of Service attacks",
            "Wysoce konfigurowalne narzędzie, które symuluje niektóre ataki Denial of Service w warstwie aplikacji",
            "slowhttptest -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "t50",
            "Multi-protocol packet injector tool for *nix systems, actually supporting 15 protocols",
            "Wieloprotokołowe narzędzie do wstrzykiwania pakietów dla systemów * nix, faktycznie obsługujące 15 protokołów",
            "t50 -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "thc-ssl-dos",
            "THC-SSL-DOS is a tool to verify the performance of SSL",
            "THC-SSL-DOS to narzędzie do weryfikacji działania SSL",
            "thc-ssl-dos -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "enumiax",
            "enumIAX is an Inter Asterisk Exchange protocol username brute-force enumerator",
            "enumIAX to moduł wyliczający nazwy użytkownika protokołu Inter Asterisk Exchange typu brute-force",
            "enumiax -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "protos-sip",
            "evaluate implementation level security and robustness of SIP implementations",
            "ocena bezpieczeństwa poziomu implementacji i solidności implementacji SIP",
            "protos-sip -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "rtpbreak",
            "With rtpbreak you can detect, reconstruct and analyze any RTP session",
            "Dzięki rtpbreak możesz wykryć, zrekonstruować i przeanalizować dowolną sesję RTP",
            "rtpbreak -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "rtpflood",
            "A command line tool used to flood any device that is processing RTP",
            "Narzędzie wiersza poleceń używane do zalewania dowolnego urządzenia przetwarzającego RTP",
            "rtpflood -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "rtpinsertsound",
            "A tool to insert audio into a specified audio (i.e. RTP) stream",
            "Narzędzie do wstawiania dźwięku do określonego strumienia audio (tj. RTP)",
            "rtpinsertsound -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "rtpmixsound",
            "A tool to mix pre-recorded audio in real-time with the audio (i.e. RTP) in the specified target audio stream",
            "Narzędzie do miksowania nagranego wcześniej dźwięku w czasie rzeczywistym z dźwiękiem w określonym docelowym strumieniu audio",
            "rtpmixsound -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "sctpscan",
            "SCTP network scanner for discovery and security",
            "Skaner sieciowy SCTP do wykrywania i zabezpieczania",
            "sctpscan -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "sipp",
            "SIPp is a free Open Source test tool / traffic generator for the SIP protocol",
            "SIPp to bezpłatne narzędzie testowe / generator ruchu typu Open Source dla protokołu SIP",
            "sipp -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "sipsak",
            "sipsak is a small command line tool for developers and administrators of SIP applications",
            "sipsak to małe narzędzie wiersza poleceń dla programistów i administratorów aplikacji SIP",
            "sipsak --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "svcrack",
            "Online password guessing tool for SIP devices",
            "Narzędzie do odgadywania haseł online dla urządzeń SIP",
            "svcrack --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "svcrash",
            "Stop unauthorized scans from svcrack/svwar tool",
            "Zatrzymaj nieautoryzowane skanowanie z narzędzia svcrack/svwar",
            "svcrash",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "svmap",
            "Scanner that searches for SIP devices on a given network",
            "Skaner wyszukujący urządzenia SIP w danej sieci",
            "svmap -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "svreport",
            "Report engine manage sessions from previous scans with SIPVicious",
            "Reportuj zarządzanie sesjami silnika z poprzednich skanów za pomocą SIPVicious",
            "svreport -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "svwar",
            "Extension line scanner",
            "Skaner linii pomocniczych",
            "svwar -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "2",
            0,
            "voiphopper",
            "VoIP Hopper is tool, written in C that rapidly runs a VLAN Hop security test",
            "VoIP Hopper to narzędzie które szybko przeprowadza test bezpieczeństwa VLAN Hop",
            "voiphopper -h",
            "kali_voiphopper",
            0
        )

        // Category 3
        insertTool(
            db,
            1,
            "3",
            0,
            "skipfish",
            "Web application security scanner",
            "Skaner bezpieczeństwa aplikacji internetowych",
            "skipfish -h",
            "kali_skipfish",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "httrack",
            "HTTrack Website Copier",
            "Kopiarka stron internetowych HTTPrack",
            "httrack -h",
            "kali_sqlmap",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "sqlmap",
            "Automatic SQL injection tool",
            "Automatyczne narzędzie do wstrzykiwania kodu SQL",
            "sqlmap --wizard",
            "kali_sqlmap",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "vulnx",
            "Intelligent CMS Shell Injector",
            "Inteligentny wtryskiwacz powłoki CMS",
            "vulnx -h",
            "vulnx",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "DotDotPWN",
            "The Directory Traversal Fuzzer",
            "Fuzzer przeglądania katalogów",
            "dotdotpwn -h",
            "dotdotpwn",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "wpscan",
            "WordPress Security Scanner",
            "Skaner bezpieczeństwa WordPress",
            "wpscan --help",
            "kali_wpscan",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "commix",
            "Automated All-in-One OS Command Injection Exploitation Tool",
            "Zautomatyzowane narzędzie do wykorzystywania iniekcji poleceń systemu operacyjnego typu 'wszystko w jednym'",
            "commix --wizard",
            "kali_commix",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "wfuzz",
            "A web application bruteforcer",
            "Aplikacja internetowa typu bruteforcer",
            "wfuzz --help",
            "kali_wfuzz",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "jboss-autopwn-linux",
            "This JBoss script deploys a JSP shell on the target JBoss AS server",
            "Ten skrypt JBoss wdraża powłokę JSP na docelowym serwerze JBoss AS",
            "jboss-linux -h",
            "kali_jboss_autopwn",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "jboss-autopwn-win",
            "This JBoss script deploys a JSP shell on the target JBoss AS server",
            "Ten skrypt JBoss wdraża powłokę JSP na docelowym serwerze JBoss AS",
            "jboss-win -h",
            "kali_jboss_autopwn",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "joomscan",
            "This package contains JoomScan, short for [Joom]la Vulnerability [Scan]ner",
            "Ten pakiet zawiera JoomScan, skrót od [Joom]la Vulnerability [Scan]ner",
            "joomscan -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "apache-users",
            "This Perl script will enumerate the usernames on any system that uses Apache with the UserDir module",
            "Ten skrypt Perla wyliczy nazwy użytkowników w każdym systemie, który używa Apache z modułem UserDir",
            "apache-users -h",
            "kali_apache_users",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "cutycapt",
            "A small cross-platform command-line utility to capture WebKit’s rendering of a web page",
            "Wieloplatformowe narzędzie do przechwytywania renderowania strony internetowej przez WebKit",
            "man cutycapt",
            "kali_cutycapt",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "dirb",
            "DIRB is a Web Content Scanner",
            "DIRB to skaner treści internetowych",
            "man dirb",
            "kali_dirb",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "dirbuster",
            "DirBuster is a java application designed to brute force directories and files names on web/application servers",
            "DirBuster to aplikacja Java przeznaczona do brutalnej siły katalogów i nazw plików na serwerach WWW/aplikacji",
            "dirbuster -h",
            "kali_dirbuster",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "ffuf",
            "ffuf is a fast web fuzzer written in Go that allows typical directory discovery",
            "ffuf to szybki fuzzer sieciowy napisany w Go, który umożliwia typowe wykrywanie katalogów",
            "ffuf -h",
            "kali_ffuf",
            0
        )
        insertTool(
            db,
            1,
            "3",
            0,
            "apache-users",
            "This Perl script will enumerate the usernames on any system that uses Apache with the UserDir module",
            "Ten skrypt Perla wyliczy nazwy użytkowników w każdym systemie, który używa Apache z modułem UserDir",
            "apache-users -h",
            "kali_apache_users",
            0
        )

        // Category 4
        insertTool(
            db,
            1,
            "4",
            0,
            "tnscmd10g",
            "A tool to prod the oracle tnslsnr process on port 1521/tcp",
            "Narzędzie do uruchamiania procesu Oracle tnslsnr na porcie 1521/tcp",
            "tnscmd10g",
            "kali_tnscmd10g",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "sidguesser",
            "Guesses sids/instances against an Oracle database according to a predefined dictionary file",
            "Zgaduje sid/instancje w stosunku do bazy danych Oracle zgodnie z predefiniowanym plikiem słownika",
            "sidguess",
            "kali_sidguesser",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "oscanner",
            "Oscanner is an Oracle assessment framework developed in Java",
            "Oscanner to środowisko oceny firmy Oracle opracowane w Javie",
            "oscanner",
            "kali_oscanner",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "sqlninja",
            "SQL Injection Tool ",
            "Narzędzie do wstrzykiwania kodu SQL",
            "sqlninja",
            "kali_sqlninja",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "mdb-sql",
            "mdb-sql is a utility program distributed with MDB Tools",
            "mdb-sql to program narzędziowy dystrybuowany z narzędziami MDB",
            "mdb-sql -h",
            "kali_mdb_sql",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "hfind",
            "Lookup a hash value in a hash database",
            "Wyszukaj wartość hasha w bazie danych hashy",
            "hfind",
            "kali_hfind",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "sqlsus",
            "MySQL injection and takeover tool, written in perl",
            "Narzędzie do wstrzykiwania i przejmowania MySQL, napisane w Perlu",
            "sqlsus -h",
            "kali_sqlsus",
            0
        )
        insertTool(
            db,
            1,
            "4",
            0,
            "sqlsus",
            "Populate a SQLite database with metadata from a disk image",
            "Wypełnij bazę danych metadanymi z obrazu dysku",
            "tsk_loaddb",
            "kali_tsk_loaddb",
            0
        )

        // Category 5
        insertTool(
            db,
            1,
            "5",
            0,
            "hydra",
            "Very fast network login cracker with many protocols support",
            "Bardzo szybki cracker logowania do sieci z obsługą wielu protokołów",
            "hydra --help",
            "kali_hydra",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "rcracki_mt",
            "RainbowCrack (improved, multi-threaded)",
            "RainbowCrack (ulepszony, wielowątkowy)",
            "rcracki_mt",
            "kali_rcracki_mt",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "medusa",
            "Medusa is intended to be a speedy, massively parallel, modular, login brute-forcer",
            "Medusa ma być szybkim, masowo równoległym, modułowym narzędziem do brutalnego logowania",
            "medusa -h",
            "kali_medusa",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "crunch",
            "Generate wordlists from a character set",
            "Generuj listy słów z zestawu znaków",
            "crunch",
            "kali_crunch",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "john",
            "Advanced & fast offline password cracker",
            "Zaawansowany i szybki łamacz haseł offline",
            "john",
            "kali_john",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "wordlists",
            "List kali linux wordlists",
            "Lista list słów kali linux",
            "wordlists",
            "kali_wordlists",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "hashcat",
            "World's fastest and most advanced password recovery utility ",
            "Najszybsze i najbardziej zaawansowane narzędzie do odzyskiwania hasła na świecie",
            "hashcat --help",
            "kali_hashcat",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "ncrack",
            "Network authentication cracking tool",
            "Narzędzie do łamania uwierzytelniania sieciowego",
            "ncrack -h",
            "kali_ncrack",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "cewl",
            "Custom word list generator",
            "Generator niestandardowej listy słów",
            "cewl --help",
            "kali_cewl",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "chntpw",
            "change password of a user in a Windows SAM file or invoke registry editor",
            "zmień hasło użytkownika w pliku Windows SAM lub wywołaj edytor rejestru",
            "chntpw -h",
            "kali_chntpw",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "fcrackzip",
            "fcrackzip is a fast password cracker partly written in assembler",
            "fcrackzip to szybkie narzędzie do łamania haseł, częściowo napisane w asemblerze",
            "fcrackzip -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "hashid",
            "Identify the different types of hashes used to encrypt data and especially passwords",
            "Zidentyfikuj różne typy skrótów używanych do szyfrowania danych, a zwłaszcza haseł",
            "hashid -h",
            "kali_hashid",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "hash-identifier",
            "Software to identify the different types of hashes used to encrypt data and especially passwords",
            "Identyfikuj różne skróty używane do szyfrowania danych, a zwłaszcza haseł",
            "hash-identifier -h",
            "kali_hash_identifier",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "samdump2",
            "Dump Windows 2k/NT/XP password hashes from a SAM file, using the syskey bootkey from the system hive",
            "Zrzuć skróty haseł systemu Windows 2k/NT/XP z pliku SAM, używając klucza rozruchowego syskey",
            "samdump2 -h",
            "kali_samdump2",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "sipdump",
            "Dump SIP digest authentications to a file",
            "Zrzuć uwierzytelnienia skrótu SIP do pliku",
            "sipdump -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "sipcrack",
            "Bruteforce the user password using the dump file generated by sipdump",
            "Bruteforce hasło użytkownika przy użyciu pliku zrzutu wygenerowanego przez sipdump",
            "sipcrack -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "sucrack",
            "A tool for cracking local user accounts via wordlist bruteforcing su",
            "Narzędzie do łamania lokalnych kont użytkowników za pomocą brutalnej siły słownej su",
            "sucrack -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "truecrack",
            "TrueCrack is a bruteforce password cracker for TrueCrypt (Copyright) volume",
            "TrueCrack to narzędzie do łamania haseł metodą bruteforce dla woluminu TrueCrypt (copyright)",
            "truecrack -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "patator",
            "Patator is a multi-purpose brute-forcer, with a modular design and a flexible usage",
            "Patator to wielozadaniowy brute-forcer o modułowej konstrukcji i elastycznym zastosowaniu",
            "patator -h",
            "kali_patator",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "thc-pptp-bruter",
            "Brute force program against pptp vpn endpoints (tcp port 1723). Fully standalone",
            "Program brutalnej siły przeciwko punktom końcowym VPN pptp (port tcp 1723). W pełni samodzielny",
            "thc-pptp-bruter -h",
            "kali_thc_pptp_bruter",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "pth",
            "Display all passing-the-hash tools",
            "Wyświetl wszystkie narzędzia do przekazywania skrótu",
            "ls /usr/bin/pth-*",
            "kali_pth",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "statsgen",
            "PACK util. Password Statistical Analysis tool",
            "Narzędzie PACK. Narzędzie do analizy statystycznej haseł",
            "statsgen -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "maskgen",
            "PACK util. Generate passwords masks",
            "PACK util. Generuj maski haseł",
            "maskgen -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "policygen",
            "PACK util. Analyze and Generate password masks according to a password policy",
            "PACK util. Analizuj i generuj maski haseł zgodnie z polityką haseł",
            "policygen -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "rsmangler",
            "RSMangler will take a wordlist and perform various manipulations on it",
            "RSMangler weźmie listę słów i wykona na niej różne manipulacje",
            "rsmangler -h",
            "kali_rsmangler",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "MaskProcessor",
            "High-Performance Advanced Word Generator",
            "Wysokowydajny, zaawansowany generator słów",
            "mp64 -h",
            "maskprocessor",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "SSH Audit",
            "SSH server & client configuration auditing",
            "Audyt konfiguracji serwera i klienta SSH",
            "ssh-audit -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "5",
            0,
            "Bopscrk",
            "Generates smart and powerful wordlists",
            "Generuje inteligentne i wydajne listy słów",
            "bopscrk -h",
            "kali_menu",
            0
        )

        // Category 6
        insertTool(
            db,
            1,
            "6",
            0,
            "cowpatty",
            "Brute-force dictionary attack against WPA-PSK",
            "Brutalny atak słownikowy na WPA-PSK",
            "cowpatty",
            "kali_cowpatty",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "freeradius-wpe",
            "FreeRADIUS is a high-performance RADIUS server",
            "FreeRADIUS to wysokowydajny serwer RADIUS",
            "freeradius -h",
            "wireless",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "pixiewps",
            "Offline Wi-Fi Protected Setup bruteforce tool",
            "Narzędzie bruteforce w trybie offline Wi-Fi Protected Setup",
            "pixiewps -h",
            "kali_pixiewps",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "reaver",
            "WReaver performs a brute force attack against an access point’s WiFi Protected Setup pin number",
            "WReaver przeprowadza brutalny atak na numer PIN chronionej konfiguracji WiFi punktu dostępowego",
            "reaver -h",
            "kali_reaver",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "kismet",
            "Wireless network and device detector, sniffer, wardriving tool, and WIDS framework",
            "Wykrywacz sieci i urządzeń bezprzewodowych, sniffer, narzędzie Wardriving i platforma WIDS",
            "kismet -h",
            "kali_kismet",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mdk3",
            "Wireless attack tool for IEEE 802.11 networks",
            "Narzędzie do ataków bezprzewodowych dla sieci IEEE 802.11",
            "mdk3 --help",
            "kali_mdk3",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mdk4",
            "Wireless attack tool for IEEE 802.11 networks",
            "Narzędzie do ataków bezprzewodowych dla sieci IEEE 802.11",
            "mdk4 --help",
            "kali_mdk3",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "wifite",
            "Python script to automate wireless auditing using aircrack-ng tools",
            "Skrypt Pythona do automatyzacji audytu sieci bezprzewodowych za pomocą narzędzi aircrack-ng",
            "wifite --help -vv",
            "kali_wifite",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "asleap",
            "actively recover LEAP/PPTP passwords",
            "aktywnie odzyskuj hasła LEAP/PPTP",
            "asleap -h",
            "kali_asleap",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "bully",
            "Bully is a new implementation of the WPS brute force attack, written in C",
            "Bully to nowa implementacja ataku siłowego WPS, napisana w C",
            "bully --help",
            "kali_bully",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "aircrack-ng",
            "A 802.11 WEP / WPA-PSK key cracker",
            "Łamacz kluczy 802.11 WEP / WPA-PSK",
            "aircrack-ng --help",
            "kali_aircrack_ng",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "EAPHammer",
            "Targeted evil twin attacks against WPA2-Enterprise networks",
            "Ukierunkowane ataki typu „evil twin” na sieci WPA2-Enterprise",
            "eaphammer -h",
            "eaphammer",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "oneshot",
            "Run WPS PIN attacks without monitor mode with the wpa_supplicant",
            "Przeprowadzaj ataki WPS PIN bez trybu monitorowania za pomocą wpa_supplicant",
            "cd /root/OneShot && python3 oneshot.py -i wlan0",
            "wireless",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "eapmd5pass",
            "Brute force password selection for EAP-MD5 authentication exchanges",
            "Brutalny wybór hasła do wymiany uwierzytelniania EAP-MD5",
            "eapmd5pass",
            "wireless",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "wifi-honey",
            "Wi-Fi access point for pentest purposes",
            "Punkt dostępowy Wi-Fi do najświętszych celów",
            "wifi-honey -h",
            "wireless",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "spooftooph",
            "Automates spoofing or cloning Bluetooth device Name, Class, and Address",
            "Automatyzuje fałszowanie lub klonowanie nazwy, klasy i adresu urządzenia Bluetooth",
            "spooftooph",
            "kali_spooftooph",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "bluelog",
            "Bluetooth scanner and logger",
            "Skaner Bluetooth i rejestrator",
            "bluelog --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "blueranger",
            "Simple Bash script which uses Link Quality to locate Bluetooth device radios",
            "Skrypt wykorzystuje jakość łącza do lokalizowania radiotelefonów urządzeń Bluetooth",
            "blueranger --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "bluesnarfer",
            "A bluetooth bluesnarfing Utility",
            "Narzędzie do bluesnarfingu Bluetooth",
            "blusnarfer --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "BTScanner",
            "Extract as much information as possible from a BT device without the requirement to pair",
            "Wydobądź jak najwięcej informacji z urządzenia BT bez konieczności parowania",
            "btscanner --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "Crackle",
            "Exploits a flaw in the BLE pairing process that allows to guess or very quickly brute force the Temporary Key",
            "Wykorzystuje lukę w procesie parowania BLE, która pozwala odgadnąć klucz tymczasowy",
            "crackle --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "RedFang",
            "Fang is a small proof-of-concept application to find non discoveredable bluetooth devices",
            "Fang to mała aplikacja sprawdzająca słuszność koncepcji do znajdowania niewykrywalnych urządzeń Bluetooth",
            "fang --help",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "ubertooth util",
            "command line utility for Ubertooth Zero and Ubertooth One",
            "Narzędzie wiersza poleceń dla Ubertooth Zero i Ubertooth One",
            "ubertooth-util -h",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "GATTTool",
            "Tool for Bluetooth Low Energy device",
            "Narzędzie do urządzenia Bluetooth Low Energy",
            "gatttool -h",
            "bluetooth",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mfcuk",
            "Various tools based on and around libnfc and crapto1, with emphasis on Mifare Classic NXP/Philips RFID cards",
            "Różne narzędzia oparte na libnfc i crapto1 i wokół nich, z naciskiem na karty Mifare Classic NXP/Philips RFID",
            "mfcuk --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mfoc",
            "This program allow one to recover authentication keys from MIFARE Classic card",
            "Ten program pozwala odzyskać klucze uwierzytelniające z karty MIFARE Classic.",
            "mfoc --help",
            "kali_mfoc",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mfterm",
            "The Mifare Terminal",
            "Terminal Mifare",
            "mfterm -h",
            "kali_mfterm",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "mifare-classic-format",
            "Libfreefare util. Erases mifare device",
            "Narzędzie Libfreefare. Usuwa urządzenie Mifare",
            "mifare-classic-format --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "nfc-list",
            "Libnfc-bin util. List NFC targets",
            "Narzędzie Libnfc-bin. Lista celów NFC",
            "nfc-list --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "nfc-mfclassic",
            "Libnfc-bin util. MIFARE Classic command line tool",
            "Narzędzie Libnfc-bin. Narzędzie wiersza poleceń MIFARE Classic",
            "nfc-mfclassic --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "chirp",
            "CHIRP Radio Programming Tool",
            "Narzędzie do programowania radia CHIRP",
            "chirp -h",
            "kali_chirp",
            0
        )
        insertTool(
            db,
            1,
            "6",
            0,
            "rfcat",
            "Rfcat is a sub GHz analysis tool",
            "Rfcat to narzędzie do analizy poniżej GHz",
            "rfcat -h",
            "kali_menu",
            0
        )

        // Category 7
        insertTool(
            db,
            1,
            "7",
            0,
            "apktool",
            "Tool for reverse engineering Android apk files",
            "Narzędzie do inżynierii wstecznej plików apk systemu Android",
            "apktool",
            "kali_apktool",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "dex2jar",
            "Tools to work with android .dex and java .class files",
            "Narzędzia do pracy z plikami Android .dex i java .class",
            "d2j-dex2jar -h",
            "kali_dex2jar",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "NASM shell",
            "A shell for NASM assembler & disassembler",
            "Powłoka dla asemblera i deasemblera NASM",
            "msf_nasm_shell",
            "kali_metasploit_framework",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "clang",
            "The Clang C, C++, and Objective-C compiler",
            "Kompilator Clang C, C++ i Objective-C",
            "clang --help",
            "kali_clang",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "rizin",
            "Reverse engineering framework and command-line toolset",
            "Struktura inżynierii wstecznej i zestaw narzędzi wiersza poleceń",
            "rizin -h",
            "kali_rizin",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "radare2",
            "Advanced commandline hexadecimal editor, disassembler and debugger",
            "Zaawansowany szesnastkowy edytor wiersza poleceń, dezasembler i debugger",
            "olradare2 -h",
            "kali_radare2",
            0
        )
        insertTool(
            db,
            1,
            "7",
            0,
            "clang++",
            "The Clang C, C++, and Objective-C compiler",
            "Kompilator Clang C, C++ i Objective-C",
            "clang++ --help",
            "kali_clang",
            0
        )

        // Category 8
        insertTool(
            db,
            1,
            "8",
            0,
            "beef start",
            "Run Beef-XSS framework on localhost",
            "Uruchom platformę Beef-XSS na hoście lokalnym",
            "beef-xss",
            "kali_beef_xss",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "searchsploit",
            "Search the exploitdb",
            "Przeszukaj exploitdb",
            "searchsploit",
            "kali_exploitdb",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "Msfpc",
            "msf payload creator",
            "twórca ładunku msf",
            "msfpc",
            "kali_msfpc",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "metasploit framework",
            "Platform that supports vulnerability research, exploit development, and the creation of custom security tools",
            "Platforma wspierająca badanie luk w zabezpieczeniach, opracowywanie exploitów i tworzenie niestandardowych narzędzi bezpieczeństwa",
            "msfdb init && msfconsole",
            "kali_metasploit_framework",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "crackmapexec",
            "A swiss army knife for pentesting Windows/Active Directory environments",
            "Szwajcarski scyzoryk do pentestów środowisk Windows/Active Directory",
            "crackmapexec -h",
            "kali_crackmapexec",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "termineter",
            "Python Smart Meter Testing Framework",
            "Struktura testowania inteligentnych liczników Pythona",
            "termineter -h",
            "kali_termineter",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "davtest",
            "Davetst exploits WebDAV folders ",
            "Davtest exploituje foldery WebDAV",
            "davtest",
            "kali_davtest",
            0
        )
        insertTool(
            db,
            1,
            "8",
            0,
            "pret",
            "Printer Exploitation Toolkit, supports ps, pjl and pcl language printers",
            "Zestaw Eksploatacji Drukarek, obsługuje języki drukarek ps, pjl i pcl",
            "python3 /root/PRET/pret.py -h",
            "nhl_pret",
            0
        )

        // Category 9
        insertTool(
            db,
            1,
            "9",
            0,
            "minicom",
            "Friendly menu driven serial communication program",
            "Przyjazny program komunikacji szeregowej sterowany menu",
            "minicom -h",
            "kali_minicom",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "ettercap-text",
            "Ettercap is a comprehensive suite for man in the middle attacks",
            "Ettercap to kompleksowy pakiet do ataków typu man in the middle",
            "ettercap -h",
            "kali_ettercap",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "hamster",
            "Script to test the password strength of the Http Basic Authentication page with a brute force attack",
            "Skrypt do testowania siły hasła strony Http Basic Authentication za pomocą ataku siłowego",
            "hamster",
            "kali_hamster_sidejack",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "bettercap",
            "The Swiss Army knife for 802.11, BLE, IPv4 and IPv6 networks reconnaissance and MITM attacks",
            "Szwajcarski scyzoryk do rozpoznania sieci 802.11, BLE, IPv4 i IPv6 oraz ataków MITM",
            "sudo bettercap",
            "kali_bettercap",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "macchanger",
            "MAC Changer",
            "Zmieniacz MAC",
            "macchanger -h",
            "kali_macchanger",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "responder",
            "This tool is first an LLMNR, NBT-NS and MDNS responder",
            "To narzędzie jest przede wszystkim odpowiedzią LLMNR, NBT-NS i MDNS",
            "responder -h",
            "kali_responder",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "dnschef",
            "DNSChef is a highly configurable DNS Proxy for Penetration Testers and Malware Analysts",
            "DNSChef to wysoce konfigurowalny serwer proxy DNS dla testerów penetracyjnych i analityków złośliwego oprogramowania",
            "dnschef -h",
            "kali_dnschef",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "evilgrade",
            "Allows you to take advantage of poor upgrade implementations by injecting fake updates",
            "Pozwala wykorzystać słabe implementacje aktualizacji poprzez wstrzykiwanie fałszywych aktualizacji",
            "evilgrade",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "yersinia",
            "Yersinia is a framework for performing layer 2 attacks",
            "Yersinia to framework do przeprowadzania ataków warstwy 2",
            "yersinia -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "netsniff-ng",
            "The packet sniffing beast",
            "Bestia wąchająca pakiety",
            "netsniff-ng -h",
            "kali_netsniff_ng",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "tshark",
            "Dump and analyze network traffic",
            "Zrzuć i analizuj ruch sieciowy",
            "tshark -h",
            "kali_wireshark",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "sslsniff",
            "SSL/TLS Man-In-The-Middle attack tool",
            "Narzędzie ataku SSL/TLS Man-In-The-Middle",
            "sslsniff -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "darkstat",
            "It is a packet sniffer which runs as a background process and serves its statistics to a web browser",
            "Jest to sniffer pakietów, który działa jako proces w tle i udostępnia swoje statystyki przeglądarce internetowej",
            "darkstat --help",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "dsniff",
            "Password sniffer for several protocols",
            "Sniffer haseł dla kilku protokołów",
            "dsniff -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "hexinject",
            "Very versatile packet injector and sniffer, that provide a command-line framework for raw network access",
            "Bardzo wszechstronny iniektor pakietów i sniffer, który zapewnia platformę wiersza poleceń dla surowego dostępu do sieci",
            "hexinject -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "tcpflow",
            "TCP/IP packet demultiplexer",
            "Demultiplekser pakietów TCP/IP",
            "tcpflow -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "fiked",
            "IPsec IKEv1 PSK+XAUTH MitM attack daemon",
            "Demon ataku IPsec IKEv1 PSK+XAUTH MitM",
            "fiked -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "rebind",
            "Rebind is a tool that implements the multiple A record DNS rebinding attack",
            "Rebind to narzędzie, które implementuje wielokrotny atak ponownego wiązania rekordu DNS",
            "dns-rebind -h",
            "kali_rebind",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "sniffjoke",
            "a client-only layer of protection from the wiretap/sniff/IDS analysis ",
            "tylko dla klienta warstwa ochrony przed podsłuchem/sniffem/analizą IDS",
            "sniffjoke -h",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "sslsplit",
            "SSLsplit is a tool for man-in-the-middle attacks against SSL/TLS encrypted network connections",
            "SSLsplit to narzędzie do ataków typu man-in-the-middle na szyfrowane połączenia sieciowe SSL/TLS",
            "sslsplit -h",
            "kali_sslsplit",
            0
        )
        insertTool(
            db,
            1,
            "9",
            0,
            "tcpreplay",
            "Test the performance of a NIDS by replaying real background network traffic in which to hide attacks",
            "Przetestuj wydajność NIDS, odtwarzając rzeczywisty ruch sieciowy w tle, aby ukryć ataki",
            "tcpreplay -h",
            "kali_tcpreplay",
            0
        )

        // Category 10
        insertTool(
            db,
            1,
            "10",
            0,
            "impacket",
            "Impacket is a collection of Python3 classes focused on providing access to network packets",
            "Impacket to zbiór klas Python3 skupionych na zapewnianiu dostępu do pakietów sieciowych",
            "(cd /usr/bin/ && ls --color=auto impacket-*)",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "powersploit",
            "PowerShell Post-Exploitation Framework",
            "Struktura poeksploatacyjna programu PowerShell",
            "powersploit",
            "kali_powersploit",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "powershell empire",
            "Powershell Empire is a PowerShell and Python post-exploitation agent",
            "Powershell Empire to agent poeksploatacyjny PowerShell i Python",
            "powershell-empire -h",
            "kali_powershell_empire",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "backdoor-factory",
            "Injects shellcode into win32/64 PE files, 32/64bits ELF binaries, to continue normal file execution",
            "Wstrzykuje kod powłoki do plików Win32/64 PE, 32/64-bitowych plików binarnych ELF, aby kontynuować normalne wykonywanie plików",
            "backdoor-factory -h",
            "kali_backdoor_factory",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "mimikatz",
            "Display passwords of currently logged-in Windows users in plaintext",
            "Wyświetl hasła aktualnie zalogowanych użytkowników systemu Windows w postaci zwykłego tekstu",
            "mimikatz",
            "kali_mimikatz",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "evil-winrm",
            "Pwn the Windows Remote Managment",
            "Pwnuj zdalne zarządzanie systemem Windows",
            "evil-winrm -h",
            "kali_evil_winrm",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "proxychains4",
            "Redirect connections through proxy servers",
            "Przekieruj połączenia przez serwery proxy",
            "proxychains4",
            "kali_proxychains",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "nishang",
            "Collection of PowerShell scripts and payloads",
            "Zbiór skryptów i ładunków programu PowerShell",
            "nishang",
            "kali_nishang",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "exe2hex",
            "Encodes an executable binary file into ASCII text format",
            "Koduje wykonywalny plik binarny do formatu tekstowego ASCII",
            "exe2hex",
            "kali_exe2hexbat",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "weevely",
            "Weaponized web shell",
            "Uzbrojona powłoka internetowa",
            "weevely",
            "kali_weevely",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "dbd",
            "A Netcat-clone, designed to be portable and offer strong encryption",
            "Klon Netcat, zaprojektowany z myślą o przenośności i oferujący silne szyfrowanie",
            "dbd -h",
            "kali_dbd",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "sbd",
            "A Netcat-clone, designed to be portable and offer strong encryption",
            "Klon Netcat, zaprojektowany z myślą o przenośności i oferujący silne szyfrowanie",
            "sbd -h",
            "kali_sbd",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "dns2tcpc",
            "A tunneling tool that encapsulate TCP traffic over DNS",
            "Narzędzie do tunelowania, które hermetyzuje ruch TCP przez DNS",
            "dns2tcpc -h",
            "kali_dns2tcp",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "dns2tcpd",
            "A tunneling tool that encapsulate TCP traffic over DNS",
            "Narzędzie do tunelowania, które hermetyzuje ruch TCP przez DNS",
            "dns2tcpd -h",
            "kali_dns2tcp",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "iodine",
            "Iodine lets you tunnel IPv4 data through a DNS server",
            "Jod umożliwia tunelowanie danych IPv4 przez serwer DNS",
            "iodine -h",
            "kali_iodine",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "miredo",
            "Creates a Teredo tunneling interface for encapsulation of IPv6 over UDP",
            "Tworzy interfejs tunelowania Teredo do enkapsulacji IPv6 przez UDP",
            "miredo -h",
            "kali_miredo",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "proxytunnel",
            "Build generic tunnels through HTTPS proxies using HTTP authentication",
            "Tworzenie ogólnych tuneli przez serwery proxy HTTPS przy użyciu uwierzytelniania HTTP",
            "proxytunnel -h",
            "kali_proxytunnel",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "ptunnel",
            "tunnel TCP connections to a remote host using ICMP echo request and reply packet",
            "tunelowanie połączeń TCP do zdalnego hosta za pomocą żądania echa ICMP i pakietu odpowiedzi",
            "ptunnel -h",
            "kali_ptunnel",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "pwnat",
            "Exploit property of NAT translation tables",
            "Wykorzystanie właściwości tabel translacji NAT",
            "pwnat -h",
            "kali_pwnat",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "sslh",
            "sslh lets one accept HTTPS, SSH, OpenVPN, tinc and XMPP connections on the same port",
            "sslh pozwala akceptować połączenia HTTPS, SSH, OpenVPN, tinc i XMPP na tym samym porcie",
            "sslh -h",
            "kali_sslh",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "stunnel4",
            "TLS offloading and load-balancing proxy",
            "Proxy proxy odciążający i równoważący obciążenie TLS",
            "man stunnel",
            "kali_stunnel4",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "udptunnel",
            "Tunnel UDP packets bi-directionally over a TCP connection",
            "Dwukierunkowe tunelowanie pakietów UDP przez połączenie TCP",
            "udptunnel -h",
            "kali_udptunnel",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "laudanum",
            "Collection of injectable web files",
            "Zbiór plików internetowych do wstrzykiwania",
            "laudanum",
            "kali_laudanum",
            0
        )
        insertTool(
            db,
            1,
            "10",
            0,
            "webacoo",
            "Scripts for creating Web backdoors using cookies, with module support",
            "Skrypty do tworzenia backdoorów sieciowych z wykorzystaniem plików cookie, z obsługą modułów",
            "webacoo -h",
            "kali_menu",
            0
        )

        // Category 11
        insertTool(
            db,
            1,
            "11",
            0,
            "yara",
            "Find files matching patterns and rules written in a special-purpose language.",
            "Znajdź pliki pasujące do wzorców i reguł napisanych w języku specjalnego przeznaczenia",
            "yara --help",
            "kali_yara",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "unhide",
            "An easy way to hide/unhide files",
            "Łatwy sposób na ukrywanie/odkrywanie plików",
            "unhide",
            "kali_menu",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "rkhunter",
            "RootKit Hunter",
            "Łowca rootkitów",
            "rkhunter -h",
            "kali_rkhunter",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "foremost",
            "Recover lost files based on their headers, footers, and internal data structures",
            "Odzyskaj utracone pliki na podstawie ich nagłówków, stopek i wewnętrznych struktur danych",
            "foremost -h",
            "kali_foremost",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "ssdeep",
            "Computes context triggered piecewise hashes (fuzzy hashes)",
            "Oblicza skróty wyzwalane kontekstem (rozmyte skróty)",
            "ssdeep -h",
            "kali_ssdeep",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "autopsy",
            "Run Autopsy Forensic Browser on localhost",
            "Uruchom przeglądarkę Autopsy Forensic Browser na hoście lokalnym",
            "autopsy",
            "kali_autopsy",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "galleta",
            "Tool to extract information from MSIE cookie files",
            "Narzędzie do wydobywania informacji z plików cookie MSIE",
            "galleta",
            "kali_galleta",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "bulk_extractor",
            "A high-performance flexible digital forensics program",
            "Elastyczny program kryminalistyczny o wysokiej wydajności",
            "bulk_extractor -h",
            "kali_bulk_extractor",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "chkrootkit",
            "The chkrootkit security scanner searches for signs that the system is infected with a ‘rootkit’",
            "Skaner bezpieczeństwa chrootkit wyszukuje oznaki, że system jest zainfekowany rootkitem",
            "chkrootkit -h",
            "kali_chkrootkit",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "binwalk",
            "Tool for searching binary images for embedded files and executable code",
            "Narzędzie do wyszukiwania obrazów binarnych w poszukiwaniu osadzonych plików i kodu wykonywalnego",
            "binwalk -h",
            "kali_binwalk",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "hashdeep",
            "Compute MD5, SHA1, SHA256, tiger and whirlpool hashsums of arbitrary number of files recursively",
            "Oblicz rekurencyjnie sumy mieszające MD5, SHA1, SHA256, tygrysa i wiru dowolnej liczby plików",
            "hashdeep -h",
            "kali_hashdeep",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "img_stat",
            "Display details of an image file",
            "Wyświetl szczegóły pliku obrazu",
            "img_stat",
            "kali_img_stat",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "istat",
            "Display details of a meta-data structure (i.e. inode)",
            "Wyświetl szczegóły struktury metadanych (tj. i-węzła)",
            "istat",
            "kali_istat",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "jadx",
            "Convert dalvik bytecode from various types of Android app files (APK, DEX, AAR, and ZIP)",
            "Konwertuj kod bajtowy dalvik z różnych typów plików aplikacji na Androida (APK, DEX, AAR i ZIP)",
            "jadx -h",
            "kali_jadx",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "mactime",
            "Create an ASCII time line of file activity",
            "Utwórz oś czasu ASCII aktywności pliku",
            "mactime",
            "kali_mactime",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "pdfid",
            "It scans files for specific PDF keywords to help identify documents that contain JS or perform an action when opened",
            "Skaunuj plik w poszukiwaniu określonych słów kluczowych w celu zidentyfikowania dokumentów PDF, które zawierają np. JS lub wykonują akcję po otwarciu",
            "pdfid -h",
            "kali_pdfid",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "pdf-parser",
            "Parse a PDF document to identify the fundamental elements used in the analyzed file",
            "przeanalizuj dokument PDF, aby zidentyfikować podstawowe elementy w nim użyte",
            "pdf-parser -h",
            "kali_pdf_parser",
            0
        )
        insertTool(
            db,
            1,
            "11",
            0,
            "sigfind",
            "It finds a binary signature in a file",
            "To narzędzie znajduje binarny podpis w pliku",
            "sigfind",
            "kali_sigfind",
            0
        )

        // Category 12
        insertTool(
            db,
            1,
            "12",
            0,
            "cutycapt",
            "Utility to capture WebKit's rendering of a web page",
            "Narzędzie do przechwytywania renderowania strony internetowej przez WebKit",
            "cutycapt --help",
            "kali_cutycapt",
            0
        )
        insertTool(
            db,
            1,
            "12",
            0,
            "metagoofil",
            "Designed for extracting metadata of public documents belonging to a target company",
            "Przeznaczony do wydobywania metadanych dokumentów publicznych należących do firmy docelowej",
            "metagoofil",
            "kali_metagoofil",
            0
        )
        insertTool(
            db,
            1,
            "12",
            0,
            "pipal",
            "It give you the stats and the information to help you analyse the passwords",
            "Daje ci statystyki i informacje, które pomogą ci przeanalizować hasła",
            "pipal -h",
            "kali_pipal",
            0
        )

        // Category 13
        insertTool(
            db,
            1,
            "13",
            0,
            "SET",
            "An tool aimed at penetration testing around Social-Engineering",
            "Narzędzie mające na celu przeprowadzanie testów penetracyjnych dotyczących inżynierii społecznej",
            "setoolkit",
            "kali_set",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "Evilginx2",
            "Phishing login credentials along with session cookies, allowing for the bypass of 2FA",
            "Dane logowania do phishingu wraz z sesyjnymi plikami cookie, pozwalającymi na ominięcie 2FA",
            "evilginx2",
            "nhl_ginx",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "z-phisher",
            "An automated phishing tool with 30+ templates",
            "Zautomatyzowane narzędzie do phishingu z ponad 30 szablonami",
            "cd /root/zphisher && bash zphisher.sh",
            "phishing",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "modlishka",
            "Reverse Proxy. 2FA authentication phishing",
            "Odwrotny serwer proxy. Wyłudzanie informacji o uwierzytelnianiu 2FA",
            "cd /root/Modlishka && ./dist/proxy -h",
            "phishing",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "GOPhish",
            "Open-source phishing toolkit designed for businesses and penetration testers",
            "Zestaw narzędzi do phishingu typu open source przeznaczony dla firm i testerów penetracyjnych",
            "cd /root/gophish && ./gophish",
            "nhl_gophish",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "Maskphish",
            "Simple bash script to hide phishing URL under a normal looking URL",
            "prosty skrypt bash do ukrywania adresu URL phishingu pod normalnie wyglądającym adresem URL",
            "cd /root/maskphish && bash maskphish.sh",
            "nhl_maskphish",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "URLCrazy",
            "URL hijacking, Phishing, and Corporate Espionage",
            "Przejmowanie adresów URL, phishing i szpiegostwo korporacyjne",
            "urlcrazy -h",
            "urlcrazy",
            0
        )
        insertTool(
            db,
            1,
            "13",
            0,
            "DNSTwist",
            "Permutation Engine for detecting homograph Phishing Attacks",
            "Silnik permutacji do wykrywania ataków phishingowych na podstawie homografów",
            "dnstwist -h",
            "antiphishing",
            0
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    val database: SQLiteDatabase
        get() = writableDatabase

    companion object {
        private const val DB_NAME = "nhlauncherdb"
        private const val DB_VERSION = 1
        private var sInstance: DBHandler? = null

        fun insertTool(
            db: SQLiteDatabase,
            system: Int,
            category: String?,
            favourite: Int,
            name: String?,
            descriptionEN: String?,
            descriptionPL: String?,
            cmd: String?,
            icon: String?,
            usage: Int
        ) {
            val toolValues = ContentValues()
            toolValues.put("SYSTEM", system)
            toolValues.put("CATEGORY", category)
            toolValues.put("FAVOURITE", favourite)
            toolValues.put("NAME", name)
            toolValues.put("DESCRIPTION_EN", descriptionEN)
            toolValues.put("DESCRIPTION_PL", descriptionPL)
            toolValues.put("CMD", cmd)
            toolValues.put("ICON", icon)
            toolValues.put("USAGE", usage)
            db.insert("TOOLS", null, toolValues)
        }

        fun updateTool(
            db: SQLiteDatabase,
            system: Int,
            category: String?,
            favourite: Int,
            name: String,
            descriptionEn: String?,
            descriptionPl: String?,
            cmd: String?,
            icon: String?,
            usage: Int
        ) {
            val toolValues = ContentValues()
            toolValues.put("SYSTEM", system)
            toolValues.put("CATEGORY", category)
            toolValues.put("FAVOURITE", favourite)
            toolValues.put("NAME", name)
            toolValues.put("DESCRIPTION_EN", descriptionEn)
            toolValues.put("DESCRIPTION_PL", descriptionPl)
            toolValues.put("CMD", cmd)
            toolValues.put("ICON", icon)
            toolValues.put("USAGE", usage)
            db.update("TOOLS", toolValues, "NAME=?", arrayOf(name))
        }

        // Update FAVOURITE field
        fun updateToolFavorite(db: SQLiteDatabase, name: String, favorite: Int) {
            val values = ContentValues()
            values.put("FAVOURITE", favorite)
            db.update("TOOLS", values, "NAME=?", arrayOf(name))
        }

        // Update USAGE field
        fun updateToolUsage(db: SQLiteDatabase, name: String, usage: Int) {
            val values = ContentValues()
            values.put("USAGE", usage)
            db.update("TOOLS", values, "NAME=?", arrayOf(name))
        }

        // Update CMD field
        fun updateToolCmd(db: SQLiteDatabase, name: String, cmd: String?) {
            val values = ContentValues()
            values.put("CMD", cmd)
            db.update("TOOLS", values, "NAME=?", arrayOf(name))
        }

        // Removes given tool by it name
        fun deleteTool(db: SQLiteDatabase, toolName: String) {
            db.delete("TOOLS", "name=?", arrayOf(toolName))
            //        db.close();
        }

        // Returns database instance, so we can reuse it
        @Synchronized
        fun getInstance(context: Context): DBHandler? {
            if (sInstance == null) {
                sInstance = DBHandler(context.applicationContext)
            }
            return sInstance
        }
    }
}