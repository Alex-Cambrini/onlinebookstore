## 1. webapp-runner.jar (shaded: io.netty:netty:3.5.5.Final)

**Nome Dipendenza/Componente:**  io.netty:netty (inglobata in webapp-runner.jar)
Nota: Netty è "shaded" all'interno di webapp-runner.jar, il che significa che non è una dipendenza diretta esterna ma è inclusa nel pacchetto. Questo può rendere l'aggiornamento più complesso.
**Descrizione:** Netty è una libreria Java ad alte prestazioni per la programmazione di applicazioni di rete asincrone e basate su eventi, utilizzata per gestire comunicazioni TCP/UDP efficienti e scalabili.
**Versione Affetta:** 3.5.5.Final (inglobata in webapp-runner.jar)  
**ID Vulnerabilità:** CVE-2019-20444, CVE-2019-20445, CVE-2015-2156, CVE-2019-16869, CVE-2020-11612, CVE-2021-37136, CVE-2021-37137, CVE-2022-41881, CVE-2023-44487  

### Natura della Flaw  
Questa è una versione obsoleta di Netty, non più supportata e contenente diverse vulnerabilità critiche. I rischi principali includono:  
- HTTP Request Smuggling (bypass autenticazione, hijacking, injection)  
- Remote DoS (OOME/StackOverflow) tramite input crafted  
- Bypass httpOnly, fuga cookie/sessione  
- CVE attivamente sfruttate (es. CVE-2023-44487, HTTP/2 Rapid Reset Attack)  

### Classificazione  
- A06:2021 – Componenti Vulnerabili e Obsoleti  
- A03:2021 – Injection (Request Smuggling)  
- A08:2021 – Software and Data Integrity Failures (Deserializzazione)  
- A01:2021 – Broken Access Control (httpOnly bypass, Smuggling)  

### Gravità  
CRITICAL (CVSS 9.0–9.8)  
Rischi: esecuzione remota, bypass sicurezza, DoS completo. Diverse CVE sono "known exploited in the wild" (CISA KEV).  

### Sfruttabilità  
Alta, "known exploited" con PoC pubblici o exploit attivi.  

### Contesto Applicativo  
webapp-runner.jar esegue onlinebookstore.war. Se esposto, la vulnerabilità è accessibile da Internet.  

### Mitigazione/Fix  
Aggiornare immediatamente webapp-runner.jar a versione che includa Netty 4.x o superiore. Nessun workaround efficace a lungo termine.  

### Raccomandazioni  
- Scansioni di vulnerabilità integrate in CI/CD  
- Aggiornamenti regolari delle dipendenze  
- Isolamento dell'applicazione  

## 2. postgresql-42.3.7

**Nome Dipendenza/Componente:** org.postgresql:postgresql (PostgreSQL JDBC Driver)  
**Descrizione:** Driver JDBC per PostgreSQL che permette alle applicazioni Java di connettersi e interagire con database PostgreSQL in modo efficiente e affidabile.
**Versione Affetta:** 42.3.7  
**ID Vulnerabilità:** CVE-2024-1597, CVE-2022-41946 (2 CVE totali)  

### Natura della Flaw  
- **CVE-2024-1597:** SQL Injection (CWE-89) attivabile solo se `PreferQueryMode=SIMPLE` è configurato (non predefinita). Richiede sintassi specifica: `-` prima di un valore numerico e un secondo placeholder stringa sulla stessa riga.  
- **CVE-2022-41946:** Information Disclosure (CWE-668, CWE-200, CWE-377). L’uso di `setText()` o `setBytea()` con `InputStream > 2k` crea file temporanei leggibili da altri utenti su sistemi Unix-like.  

### Classificazione  
- OWASP A03:2021 (Injection - SQL Injection)  
- OWASP A06:2021 (Componenti Vulnerabili e Obsoleti)  

### Gravità  
- CVE-2024-1597: CRITICAL (CVSS Base Score 9.8)  
- CVE-2022-41946: MEDIUM (CVSS Base Score 5.5)  
Impatto su riservatezza, integrità e disponibilità (CVE-2024-1597) e su riservatezza locale (CVE-2022-41946).  

### Sfruttabilità  
- CVE-2024-1597: Alta – AV:N/AC:L/PR:N/UI:N  
- CVE-2022-41946: Locale – AV:L/AC:L/PR:L/UI:N  
La SQLi è sfruttabile da remoto senza autenticazione, ma solo con configurazione specifica. L’altra è locale e dipende dalla gestione dei file temporanei.  

### Contesto Applicativo  
`postgresql-42.3.7.jar` è dentro `onlinebookstore.war` (`/WEB-INF/lib/postgresql-42.3.7.jar`). Se l’applicazione è esposta e usa configurazioni vulnerabili, entrambe le falle possono essere sfruttate.  

### Mitigazione/Fix  
- Aggiornare il driver PostgreSQL JDBC a versioni non vulnerabili:  
  - 42.7.2 o superiore  
  - 42.6.1 o superiore  
  - 42.5.5 o superiore  
  - 42.4.4 o superiore  
  - 42.3.9 o superiore  
  - 42.2.28 o superiore  
- Workaround CVE-2024-1597: non usare `PreferQueryMode=SIMPLE`  
- Workaround CVE-2022-41946: su Java ≤ 1.6, impostare `java.io.tmpdir` su directory privata  

### Raccomandazioni  
- Aggiornare subito il driver JDBC alla versione più recente e sicura  
- Verificare e correggere configurazioni JDBC (query mode, directory temporanee)  
- Integrare scansioni di vulnerabilità nel processo CI/CD  
- Mantenere aggiornate tutte le dipendenze  
- Applicare il principio del minimo privilegio agli utenti database  

## 3. protobuf-java-3.11.4.jar

**Nome Dipendenza/Componente:** com.google.protobuf:protobuf-java  
**Descrizione:** Libreria Java per la serializzazione e deserializzazione di dati usando Protocol Buffers, un formato efficiente e compatto sviluppato da Google per lo scambio strutturato di dati.
**Versione Affetta:** 3.11.4  
**ID Vulnerabilità:**  
- CVE-2024-7254  
- CVE-2022-3171  
- CVE-2022-3509  
- CVE-2022-3510  
- CVE-2021-22569  

### Natura della Flaw  
- Deserializzazione insicura:  possibile Remote Code Execution (RCE) se l’input serializzato è controllato da un attaccante.
- Denial of Service (DoS): blocco applicazione (anche tramite buffer overflow) con input malevolo o troppo grande. 
- Fuga di informazioni: possibile accesso a dati sensibili durante la deserializzazione  

### Classificazione  
- OWASP A06:2021 (Componenti Vulnerabili e Obsoleti)  
- Potenzialmente OWASP A08:2021 (Failure di Integrità di Software e Dati) o A04:2021 (Insecure Design), a seconda delle CVE specifiche  

### Gravità  
HIGH, con impatti da DoS a RCE o compromissione dati  

### Sfruttabilità  
Non dichiarata come "known exploited", ma rischio significativo vista la gravità e frequenza degli attacchi su librerie di serializzazione  

### Contesto Applicativo  
`protobuf-java-3.11.4.jar` è incluso in `onlinebookstore.war`. Se l’app riceve input esterni che vengono deserializzati con questa libreria, la vulnerabilità è esposta.  

### Mitigazione/Fix  
- Aggiornare protobuf-java alla versione 3.21.7 o superiore (o alla versione che corregge tutte le CVE segnalate)

### Raccomandazioni  
- Aggiornare sempre alla versione più recente e stabile  
- Validare e sanificare rigorosamente gli input prima della deserializzazione  
- Applicare il principio del minimo privilegio nelle autorizzazioni  
- Monitorare e loggare comportamenti anomali per identificare tentativi di sfruttamento  

## 4. jettison:1.1

**Nome Dipendenza/Componente:** org.codehaus.jettison:jettison  
**Descrizione:** Jettison è una libreria che converte JSON in XML e viceversa. La versione 1.1 contiene vulnerabilità note.
**Versione Affetta:** 1.1 (inglobata in onlinebookstore.war)  
**ID Vulnerabilità:** CVE-2022-40149, CVE-2022-40150, CVE-2022-45685, CVE-2022-45693, CVE-2023-1436 (5 CVE totali)  

### Natura della Flaw  
- Iniezione XML/JSON tramite input esterno non sanificato  
- Deserializzazione insicura: input malevoli possono causare RCE, DoS o divulgazione dati  
- Denial of Service (DoS): input JSON/XML craftato che provoca crash o rallentamenti  
- XML External Entity (XXE): rischio se parser XML non disabilita entità esterne  

### Classificazione  
- OWASP A06:2021 (Componenti Vulnerabili e Obsoleti)  
- Potenzialmente OWASP A03:2021 (Injection), per iniezione o XXE  
- Potenzialmente OWASP A08:2021 (Software and Data Integrity Failures), per RCE o dati corrotti via deserializzazione  

### Gravità  
HIGH (5 CVE). Impatti possibili: DoS, compromissione integrità dati, disponibilità, esecuzione codice arbitrario, lettura di file locali sul server (XXE), esecuzione di richieste verso host interni (SSRF). 

### Sfruttabilità  
Non dichiarata come "known exploited", ma alta probabilità di attacchi noti vista la gravità e numero di CVE. Facilità di sfruttamento dipende dall’uso dell’app.  

### Contesto Applicativo  
Jettison 1.1 è presente in `/WEB-INF/lib/jettison-1.1.jar` dentro `onlinebookstore.war`. Se l’app accetta input JSON/XML da esterni, la vulnerabilità è esposta.  

### Mitigazione/Fix  
Aggiornare Jettison ad almeno la versione 1.5.4, che corregge tutte le CVE elencate

### Raccomandazioni  
- Aggiornare subito la libreria alla versione più recente
- Evitare l’uso diretto di new JSONObject(xmlString) su input non controllato.  
- Validare e sanificare rigorosamente input JSON/XML prima della deserializzazione  
- Configurare parser XML per disabilitare entità esterne (XXE)  
- Implementare monitoraggio e logging per rilevare attacchi o anomalie

## 5. mysql-connector-java-8.0.28.jar

**Nome Dipendenza/Componente:** mysql:mysql-connector-java (MySQL Connector/J)  
**Descrizione:** Driver JDBC ufficiale di MySQL per connettere applicazioni Java al database MySQL, gestendo comunicazioni, autenticazione e operazioni SQL.
**Versione Affetta:** 8.0.28  
**ID Vulnerabilità:** CVE-2023-22102 (1 CVE)

### Natura della Flaw  
- Improper Access Control (CWE-284)  
- Attaccante non autenticato con accesso rete può compromettere MySQL Connector/J
- Alcuni difetti sono legati alla gestione errata di pacchetti di handshake, che possono essere sfruttati per causare denial-of-service, memory leak, o potenziali escalation in caso di configurazioni deboli
- Richiede interazione umana da terzi  
- Attacco può portare al controllo completo del connettore

### Classificazione  
- OWASP A06:2021 (Componenti Vulnerabili e Obsoleti)  
- OWASP A01:2021 (Broken Access Control)

### Gravità  
**HIGH (CVSS 3.1 Base Score 8.3)**  
Impatto su **Confidenzialità**, **Integrità** e **Disponibilità**

### Sfruttabilità  
- Complessità: alta (richiede interazione umana)  
- Vettore: rete (AV:N)  
- Stato: **non "known exploited"**, ma rischio significativo

### Contesto Applicativo  
Incluso in `onlinebookstore.war`, il connettore gestisce le connessioni a database MySQL.  
Se esposto a rete e con configurazioni vulnerabili, è a rischio.

### Mitigazione/Fix  
- Aggiornare a `mysql-connector-java` versione **> 8.1.0**  
- Tutte le versioni **≤ 8.1.0** sono vulnerabili

### Raccomandazioni 
- Aggiornare subito alla versione più recente e sicura  
- Applicare principio del minimo privilegio sulle credenziali database  
- Isolare la rete per limitare l’accesso a database e applicazione  
- Sensibilizzare gli utenti su vettori di attacco (es. phishing)  
- Evitare configurazioni deboli come useSSL=false 
- **Monitorare i log JDBC** per rilevare connessioni sospette o anomale

## 6. commons-io:2.3

**Nome Dipendenza/Componente:** commons-io:commons-io  
**Descrizione:** Libreria Java che facilita operazioni di input/output su file e stream.  
**Versione Affetta:** 2.3 (inglobata in webapp-runner.jar)  
**ID Vulnerabilità:** CVE-2021-29425, CVE-2024-47554 (2 CVE totali)  

### Natura della Flaw  
- **CVE-2021-29425:** Directory Traversal (CWE-22, CWE-20). Il metodo `FileNameUtils.normalize` può restituire percorsi non sanitizzati, permettendo accesso o scrittura fuori dalle directory previste.  
- **CVE-2024-47554:** Denial of Service (CWE-400). La classe `XmlStreamReader` può consumare eccessive risorse CPU con input appositamente creati.  

### Classificazione  
- OWASP A06:2021 (Componenti Vulnerabili e Obsoleti)  
- Possibile OWASP A08:2021 (Software and Data Integrity Failures)  
- Possibile OWASP A01:2021 (Broken Access Control), in base al contesto  

### Gravità  
- Media (MEDIUM) per entrambe, con impatti su disponibilità, integrità e riservatezza parziali.  
- Potenziale esposizione a lettura o scrittura non autorizzata di file sensibili.  

### Sfruttabilità  
- CVE-2021-29425: sfruttabile da remoto senza autenticazione, con complessità medio-alta.  
- CVE-2024-47554: sfruttabile da remoto con complessità bassa, richiede interazione utente.  
- Dipende dal contesto applicativo e dall’uso delle funzionalità vulnerabili.  

### Contesto Applicativo  
Commons IO 2.3 è inglobata in webapp-runner.jar, usato per eseguire onlinebookstore.war. Le vulnerabilità sono rilevanti se l’app elabora file o stream da input esterni.  

### Mitigazione/Fix  
- Aggiornare webapp-runner.jar a versione che include Commons IO aggiornata, almeno alla 2.11.0 o preferibilmente 2.14.0.  

### Raccomandazioni  
- Aggiornare la libreria all’ultima versione stabile.  
- Validare e sanificare rigorosamente percorsi e contenuti di file provenienti da input esterni.  
- Usare `new File(baseDir, safeFileName).getCanonicalPath()` per prevenire path traversal.  
- Applicare whitelist per nomi file e limitare privilegi di accesso ai file per ridurre i danni in caso di sfruttamento.  

## 7. 




## 8. Gestione non sicura di IOException (getWriter)

**Nome Componente:** `BuyBooksServlet.java`  
**Descrizione:** Il metodo `res.getWriter()` può lanciare una `IOException`, ma non viene gestita. Questo comporta una potenziale interruzione anomala del flusso dell'applicazione o leak di informazioni di errore lato server.

**Riga affetta:** Linea 24  
**ID Regola Sonar:** `java:S1989`  
**Tag:** `cwe`, `error-handling`  
**File:** `src/main/java/servlets/BuyBooksServlet.java`  

### Natura della Flaw  
Il metodo `getWriter()` può fallire in caso di problemi con il flusso di output della risposta HTTP (es. connessione chiusa, errore I/O). Senza gestione esplicita dell’eccezione, l’applicazione può:  
- Rispondere in modo inconsistente  
- Esibire stack trace lato client in ambienti malconfigurati  
- Esporre dettagli del server  

### Classificazione  
- **CWE-703** – Improper Check or Handling of Exceptional Conditions  
- **OWASP A05:2021 – Security Misconfiguration**  
- **OWASP A09:2021 – Security Logging and Monitoring Failures** (se non logga l’errore)

### Gravità  
**MEDIUM**  
**Rischi:**  
- Blocchi o crash dell’applicazione  
- Leak di stack trace e dettagli interni  
- Denial of Service locale su endpoint specifici  

### Sfruttabilità  
Media. Non sfruttabile per RCE, ma può essere usata per causare malfunzionamenti, errori inaspettati o capire la struttura dell’applicazione.

### Contesto Applicativo  
Componente `BuyBooksServlet` gestisce richieste POST per l’acquisto. Se l’errore avviene, l’utente riceve una risposta HTTP corrotta e l’operazione viene interrotta in modo anomalo.

### Mitigazione/Fix  
Gestire `IOException` con un blocco `try-catch`, log sicuro, e invio di errore controllato:  
```java
try {
    PrintWriter pw = res.getWriter();
    res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
    // ...
} catch (IOException e) {
    log.error("Errore durante la scrittura della risposta", e);
    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore interno.");
    return;
}
```

## 9. Servlet Exception Handling Issue in CheckoutServlet

**Nome Dipendenza/Componente:**  Java Servlet `doPost` method in `CheckoutServlet.java`  
**Descrizione:** Il metodo `doPost` lancia eccezioni `ServletException` e `IOException` non gestite internamente, in particolare su chiamate a `include` di `RequestDispatcher`. Questo espone la servlet a problemi di stabilità e sicurezza.  
**Versione Affetta:** Codice sorgente attuale senza gestione try/catch interna sulle eccezioni `ServletException`, `IOException`  
**ID Vulnerabilità:** N/A (issue di qualità e sicurezza nel codice servlet, rilevato come SonarQube rule java:S1989)  

### Natura della Flaw  
Il metodo servlet non cattura internamente eccezioni checked obbligatorie (`ServletException`, `IOException`) nelle chiamate a `RequestDispatcher.include()`. Questo comporta:  
- Possibile propagazione incontrollata dell’eccezione al contenitore servlet, causando crash o comportamenti inattesi.  
- Rischio di denial-of-service se il contenitore si blocca o si destabilizza.  
- Esposizione di dettagli tecnici sensibili (stack trace, configurazioni) all’utente finale, compromettendo la sicurezza.  

### Classificazione  
- CWE-600: Uncaught Exception in Servlet  
- Software qualities impacted: Security, Consistency  

### Gravità  
Media-alta, per rischio DoS e leak di informazioni sensibili.

### Sfruttabilità  
Dipende dal contesto di esecuzione, ma potenzialmente elevata in caso di input malformati o condizioni anomale che causano l’eccezione.

### Contesto Applicativo  
Applicazione Java web basata su servlet, gestione login e pagamento in `CheckoutServlet.java`.

### Mitigazione/Fix  
Modificare il metodo `doPost` per catturare internamente le eccezioni `ServletException` e `IOException` in blocchi try/catch attorno alle chiamate che le possono generare (es. `RequestDispatcher.include`).  

```java
try {
    rd.include(req, res);
} catch (ServletException | IOException e) {
    // Log dell'eccezione in modo sicuro
    log.error("Errore durante l'include della risorsa", e);
    // Invio risposta HTTP di errore controllata
    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore interno al server");
    return;
}
```



## 10. Handle exception StoreException in CustomerLoginServlet#doPost

**Nome Dipendenza/Componente:** CustomerLoginServlet#doPost (Java Servlet)

**Descrizione:**  
Il metodo doPost del servlet CustomerLoginServlet chiama il metodo login di authService che può lanciare una StoreException. Attualmente questa eccezione non viene gestita internamente, ma propagata fuori dal metodo.

**Versione Affetta:** codice sorgente Java servlets, file CustomerLoginServlet.java

**ID Vulnerabilità:** SonarJava rule S1989 (Exceptions should not be thrown from servlet methods)

### Natura della Flaw  
Le eccezioni non gestite lanciate dai metodi servlet possono:  
- causare instabilità nel container servlet, portando a denial-of-service;  
- esporre informazioni sensibili (stack trace, configurazioni) all’utente finale;  
- compromettere la sicurezza e la stabilità del sistema.

### Classificazione  
- CWE-600: Uncaught Exception in Servlet  
- Sicurezza: potenziale esposizione di dati sensibili e DoS

### Gravità  
Media-alta: problemi di sicurezza e stabilità applicativa dovuti a gestione eccezioni inadeguata.

### Sfruttabilità  
Dipende dal contesto, ma lanciare eccezioni non gestite in servlet espone il sistema a vulnerabilità di tipo DoS e leak informativi.

### Contesto Applicativo  
Servlet responsabile del login cliente in un’applicazione web Java, che non gestisce StoreException durante la chiamata a authService.login.

### Mitigazione/Fix  
Circondare tutte le chiamate che possono generare eccezioni (inclusa authService.login) con blocchi try/catch all’interno del metodo doPost, catturando StoreException (e altre potenziali) e gestendole in modo appropriato (log, messaggio utente generico, redirect a pagina errore).

```java
public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    PrintWriter pw = res.getWriter();
    try {
        User user = authService.login(UserRole.CUSTOMER, uName, pWord, req.getSession());
        if (user != null) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerHome.html");
            rd.include(req, res);
            pw.println("<div id=\"topmid\"><h1>Welcome to Online <br>Book Store</h1></div>");
        } else {
            // gestione login fallito
        }
    } catch (StoreException e) {
        logger.error("Errore durante login cliente", e);
        pw.println("Errore interno, riprova più tardi.");
    }
}
```
### Raccomandazioni  
- Non propagare eccezioni fuori dai metodi doGet/doPost dei servlet.  
- Gestire tutte le eccezioni internamente con try/catch.  
- Fornire messaggi di errore user-friendly senza leak di informazioni sensibili.  
- Integrare test di sicurezza e revisione codice specifica per gestione eccezioni in servlet.
