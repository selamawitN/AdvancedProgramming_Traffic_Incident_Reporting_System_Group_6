
# TRAFFIC INCIDENT MANAGEMENT SYSTEM

## A summary of the Project Report

 ### **INTRODUCTION**

This project is called Traffic Incident Management System. We developed this system to help citizens report traffic incidents easily and to help administrators respond to emergencies faster. The system has two main parts. The first part is a web dashboard where citizens can report incidents using an interactive map. The second part is a desktop application where administrators can manage all reported incidents and receive instant alerts for critical situations.

The main problem this project solves is the slow response time when traffic accidents happen. In many cases, people do not know where to report incidents or the information does not reach the right people quickly enough. Our system allows anyone with a browser to report an incident in less than one minute. When someone reports a critical incident, all administrators receive an instant popup alert on their computers.
For this project, we used Java as the main programming language. We chose Java because it is platform independent and has strong networking libraries. For the database, we used MySQL which runs on XAMPP. The desktop interface was built using Java Swing. For the web dashboard, we wrote Java code to create an HTTP server that serves HTML, CSS, and JavaScript with the Leaflet library for maps. The system uses three different types of communication. TCP sockets handle regular client server communication on port 5000. UDP broadcast sends emergency alerts on port 6000. There is also a built in HTTP server that serves the web dashboard on port 8081.

The system follows a client server architecture. The server is the central component that handles all requests from clients. When a citizen submits an incident report through the web dashboard, the HTTP request goes to the web server which then communicates with the database. When an administrator logs into the desktop application, the TCP client connects to the TCP server. The server creates a new thread for each connected client so multiple administrators can work simultaneously.

The database stores two main tables. The users table contains administrator accounts with their email, password, and role. The incidents table stores every reported incident including the type, location, severity, description, status, reporter name, timestamp, and map coordinates. When a critical incident is reported, the system triggers a UDP broadcast. The UDPServer class sends a packet to the broadcast address on port 6000. Any administrator who is logged into the desktop application has a UDPListener thread running in the background. When this listener receives a broadcast, it immediately shows a popup window on the administrator screen.

The web dashboard is not a separate application. It is written entirely in Java inside the WebServer class. This class creates an HTTP server that listens on port 8081. When a browser requests the homepage, the WebServer class sends an HTML string that contains all the CSS, JavaScript, and map code. When the browser submits an incident form, the WebServer class receives the POST request, parses the JSON data, and saves the incident to the database. This means the entire web dashboard is served from the same Java application without needing Apache or Tomcat.

The web dashboard provides citizens with a map where they can click to select the exact location of an incident. There is also a search feature that allows users to type a place name and the map will zoom to that location. There is a button called Use My Location that gets the user current position using the browser geolocation API. The form asks for incident type, location name, severity level, the citizen name, and a description. After submitting the form, the incident appears on the admin dashboard instantly. The web dashboard also has a First Aid tab that shows instructions for CPR, bleeding control, and burns. It also displays emergency phone numbers for ambulance, fire brigade, and police.

The desktop application requires login before access. Only users with the admin role can log in. Once logged in, the administrator sees a table showing all incidents with columns for ID, type, location, severity, status, and reported by. The administrator can change the status of an incident to In Progress or Resolved. There is also a delete button to remove incidents from the system. A statistics panel at the top shows total incidents, critical incidents, and resolved incidents.

The analytics screen shows two charts. One bar chart displays incidents grouped by type. One pie chart shows the distribution of severity levels. There is also a list of the most dangerous locations based on how many incidents were reported at each place. These charts are drawn using Java2D without any external charting libraries.

The logging system writes every important action to a text file. This includes logins, incident reports, status changes, and UDP broadcasts. The logger uses file locking so that multiple threads do not corrupt the file at the same time.

There is also a watchdog thread that runs every sixty seconds. This thread checks the database for any critical incidents that are still open. If it finds any, it sends another UDP alert to remind administrators that critical incidents remain unresolved.

 ### **HOW TO TEST THE SYSTEM**

To test the system completely, the user should follow these steps.

First, open the web dashboard ( http://localhost:8081 ) in a browser. Click anywhere on the map to select a location. Then fill in the incident form. Second, look at the admin desktop application. The new incident should appear in the table with status. Third, select the incident row in the admin table. then click any of the 3 buttons down.
Fourth, click the Analytics button. A new window will open showing a bar chart and a pie chart. Fifth, click the View Logs button. A new window will open showing the last fifty log entries. Verify that the log shows the admin login, the incident report, the UDP broadcast, and the status change. Sixth, submit another incident through the web dashboard with severity Low. Verify that no UDP popup appears because only Critical incidents trigger the broadcast.



