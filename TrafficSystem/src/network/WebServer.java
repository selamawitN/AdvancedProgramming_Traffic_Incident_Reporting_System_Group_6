package network;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import database.IncidentDAO;
import models.Incident;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

public class WebServer {
    private static final int PORT = 8081;
    private static IncidentDAO dao = new IncidentDAO();

    public static void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new DashboardHandler());
            server.createContext("/api/incidents", new ApiHandler());
            server.createContext("/api/submit", new SubmitHandler());
            server.start();
            System.out.println("Web Dashboard: http://localhost:" + PORT);
        } catch (IOException e) { e.printStackTrace(); }
    }

    static class DashboardHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
<!DOCTYPE html>
<html>
<head>
    <title>Traffic System</title>
    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>
    <link rel='stylesheet' href='https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.css'/>
    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>
    <script src='https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.js'></script>
    <style>
        *{margin:0;padding:0;box-sizing:border-box;}
        body{font-family:Segoe UI,sans-serif;background:#f0f2f5;}
        .header{background:#1a1a2e;color:#fff;padding:16px 32px;display:flex;justify-content:space-between;align-items:center;}
        .header h1{font-size:20px;}
        .live-badge{background:#1D9E75;padding:4px 12px;border-radius:20px;font-size:12px;}
        .container{display:flex;min-height:calc(100vh-70px);}
        .sidebar{width:380px;background:#fff;border-right:1px solid #ddd;}
        .tab-buttons{display:flex;}
        .tab-btn{padding:14px;border:none;background:#f5f5f5;cursor:pointer;font-weight:bold;width:33%;}
        .tab-btn.active{background:#fff;border-bottom:3px solid #1D9E75;color:#1D9E75;}
        .tab-content{padding:20px;height:calc(100vh-70px);overflow-y:auto;}
        .tab-pane{display:none;}
        .tab-pane.active{display:block;}
        .form-group{margin-bottom:15px;}
        .form-group label{display:block;margin-bottom:5px;font-weight:bold;font-size:13px;}
        .form-group input,.form-group select,.form-group textarea{width:100%;padding:10px;border:1px solid #ddd;border-radius:8px;font-size:14px;}
        .submit-btn{background:#1D9E75;color:#fff;padding:12px;border:none;border-radius:8px;width:100%;font-weight:bold;cursor:pointer;font-size:14px;margin-top:10px;}
        .submit-btn:hover{background:#15855E;}
        .location-btn{background:#1a1a2e;color:#fff;padding:10px;border:none;border-radius:8px;width:100%;font-weight:bold;cursor:pointer;font-size:13px;margin-bottom:10px;}
        .location-btn:hover{background:#2d2d4e;}
        .search-box{width:100%;padding:10px;border:1px solid #ddd;border-radius:8px;font-size:14px;margin-bottom:10px;}
        .guide-card{border:1px solid #e0e0e0;border-radius:12px;margin-bottom:16px;}
        .guide-title{background:#ff6b35;color:#fff;padding:14px;cursor:pointer;font-weight:bold;}
        .guide-steps{padding:16px;display:none;background:#fffaf5;}
        .guide-steps.show{display:block;}
        .step{padding:8px 0;border-bottom:1px solid #ffe0cc;display:flex;gap:10px;}
        .step-num{background:#ff6b35;color:#fff;width:24px;height:24px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:bold;}
        .emergency-btn{background:#dc2626;color:#fff;padding:14px;border:none;border-radius:10px;width:100%;font-weight:bold;margin-bottom:20px;cursor:pointer;}
        .emergency-btn:hover{background:#b91c1c;}
        .map-area{flex:1;position:relative;}
        #map{height:100%;width:100%;}
        .stats-panel{position:absolute;bottom:20px;right:20px;background:rgba(255,255,255,0.95);padding:12px 20px;border-radius:12px;box-shadow:0 2px 10px rgba(0,0,0,0.1);z-index:1000;}
        .stats-panel div{margin:5px 0;font-size:13px;}
        .stats-panel .total{color:#1D9E75;font-weight:bold;}
        .stats-panel .critical{color:#dc2626;font-weight:bold;}
        .stats-panel .resolved{color:#059669;font-weight:bold;}
        .incident-item{padding:12px;border-bottom:1px solid #eee;cursor:pointer;}
        .incident-item:hover{background:#f5f5f5;}
        .incident-item b{color:#1a1a2e;}
        .map-hint{background:#e8f0fe;padding:8px 12px;border-radius:8px;margin-top:10px;font-size:12px;text-align:center;}
        .map-hint.active{background:#1D9E75;color:white;}
        h3{margin-bottom:20px;color:#1a1a2e;}
        .button-group{display:flex;gap:10px;margin-bottom:15px;}
        .button-group .location-btn{flex:1;margin-bottom:0;}
        @media(max-width:768px){.container{flex-direction:column;}.sidebar{width:100%;max-height:50vh;}.map-area{height:50vh;}}
    </style>
</head>
<body>
<div class='header'><h1>Traffic Incident System</h1><div class='live-badge'>LIVE</div></div>
<div class='container'>
    <div class='sidebar'>
        <div class='tab-buttons'>
            <button class='tab-btn active' onclick='switchTab("report")'>Report</button>
            <button class='tab-btn' onclick='switchTab("firstaid")'>First Aid</button>
            <button class='tab-btn' onclick='switchTab("incidents")'>Incidents</button>
        </div>
        <div class='tab-content'>
            <div id='report-pane' class='tab-pane active'>
                <h3>Report Incident</h3>
                <form id='incidentForm'>
                    <div class='form-group'><label>Type</label><select id='type' required>
                        <option>Car Collision</option><option>Traffic Jam</option><option>Flooded Road</option>
                        <option>Road Construction</option><option>Broken Traffic Light</option><option>Vehicle Fire</option>
                    </select></div>
                    <div class='form-group'><label>Location Name</label><input id='location' placeholder='Enter street name or area' required></div>
                    <div class='form-group'><label>Severity</label><select id='severity' required>
                        <option value='Low'>Low</option><option value='Moderate'>Moderate</option><option value='Critical'>Critical</option>
                    </select></div>
                    <div class='form-group'><label>Your Name</label><input id='reporterName' placeholder='Your full name' required></div>
                    <div class='form-group'><label>Description</label><textarea id='description' rows='3' placeholder='Describe what happened...'></textarea></div>
                    <div class='button-group'>
                        <button type='button' id='useMyLocationBtn' class='location-btn'>Use My Location</button>
                    </div>
                    <div id='mapHint' class='map-hint'>Search for a place or click on map</div>
                    <input type='hidden' id='latitude'>
                    <input type='hidden' id='longitude'>
                    <button type='submit' class='submit-btn'>Submit Report</button>
                </form>
                <div id='submitStatus' style='margin-top:12px;text-align:center;'></div>
            </div>
            <div id='firstaid-pane' class='tab-pane'>
                <button class='emergency-btn' onclick='alert("Emergency Services\\nAmbulance: 907\\nFire Brigade: 939\\nPolice: 991")'>CALL 907 - AMBULANCE</button>
                <div class='guide-card'><div class='guide-title' onclick='toggleGuide("cpr")'>CPR Instructions</div>
                    <div id='cpr-steps' class='guide-steps'><div class='step'><span class='step-num'>1</span>Call 907 immediately</div><div class='step'><span class='step-num'>2</span>Lay person flat on back</div><div class='step'><span class='step-num'>3</span>30 chest compressions</div><div class='step'><span class='step-num'>4</span>2 rescue breaths</div><div class='step'><span class='step-num'>5</span>Repeat until help arrives</div></div></div>
                <div class='guide-card'><div class='guide-title' onclick='toggleGuide("bleeding")'>Bleeding Control</div>
                    <div id='bleeding-steps' class='guide-steps'><div class='step'><span class='step-num'>1</span>Apply firm pressure to wound</div><div class='step'><span class='step-num'>2</span>Use clean cloth or bandage</div><div class='step'><span class='step-num'>3</span>Do not remove the cloth</div><div class='step'><span class='step-num'>4</span>Elevate injured area if possible</div><div class='step'><span class='step-num'>5</span>Call 907 if bleeding won't stop</div></div></div>
                <div class='guide-card'><div class='guide-title' onclick='toggleGuide("burn")'>Burns First Aid</div>
                    <div id='burn-steps' class='guide-steps'><div class='step'><span class='step-num'>1</span>Cool burn with cool water 10 minutes</div><div class='step'><span class='step-num'>2</span>Do not use ice or butter</div><div class='step'><span class='step-num'>3</span>Cover with clean bandage</div><div class='step'><span class='step-num'>4</span>Do not break blisters</div><div class='step'><span class='step-num'>5</span>Seek medical help immediately</div></div></div>
            </div>
            <div id='incidents-pane' class='tab-pane'><div id='incidentList'>Loading...</div></div>
        </div>
    </div>
    <div class='map-area'>
        <div id='map'></div>
        <div class='stats-panel'><strong>Live Statistics</strong><div>Total: <span class='total' id='statTotal'>0</span></div><div>Critical: <span class='critical' id='statCritical'>0</span></div><div>Resolved: <span class='resolved' id='statResolved'>0</span></div></div>
    </div>
</div>
<script>
let map, markers=[], tempMarker=null, selectedLat=null, selectedLng=null, searchControl=null;

function initMap(){
    map=L.map('map').setView([9.02497,38.74689],13);
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png').addTo(map);
    map.on('click', onMapClick);
    
    // Add search control to map
    searchControl = L.Control.geocoder({
        defaultMarkGeocode: false,
        placeholder: 'Search for a place...',
        errorMessage: 'Place not found',
        showResultIcons: true
    }).on('markgeocode', function(e){
        var center = e.geocode.center;
        var name = e.geocode.name;
        selectedLat = center.lat;
        selectedLng = center.lng;
        document.getElementById('latitude').value = selectedLat;
        document.getElementById('longitude').value = selectedLng;
        if(tempMarker) map.removeLayer(tempMarker);
        tempMarker = L.marker([selectedLat, selectedLng]).addTo(map).bindPopup(name).openPopup();
        map.setView([selectedLat, selectedLng], 16);
        document.getElementById('location').value = name.split(',')[0];
        document.getElementById('mapHint').innerHTML = 'Location set: ' + name.split(',')[0];
        document.getElementById('mapHint').classList.add('active');
    }).addTo(map);
    
    load();
    setInterval(load,5000);
}

function onMapClick(e){
    selectedLat=e.latlng.lat;
    selectedLng=e.latlng.lng;
    document.getElementById('latitude').value=selectedLat;
    document.getElementById('longitude').value=selectedLng;
    if(tempMarker) map.removeLayer(tempMarker);
    tempMarker=L.marker([selectedLat,selectedLng]).addTo(map).bindPopup('Incident location').openPopup();
    let hint=document.getElementById('mapHint');
    hint.innerHTML='Location set! Click map to change';
    hint.classList.add('active');
}

function useMyLocation(){
    if(navigator.geolocation){
        document.getElementById('mapHint').innerHTML='Getting your location...';
        navigator.geolocation.getCurrentPosition(function(position){
            selectedLat=position.coords.latitude;
            selectedLng=position.coords.longitude;
            document.getElementById('latitude').value=selectedLat;
            document.getElementById('longitude').value=selectedLng;
            if(tempMarker) map.removeLayer(tempMarker);
            tempMarker=L.marker([selectedLat,selectedLng]).addTo(map).bindPopup('Your location').openPopup();
            map.setView([selectedLat,selectedLng],15);
            document.getElementById('mapHint').innerHTML='Location set to your current position!';
            document.getElementById('mapHint').classList.add('active');
        }, function(error){
            let errorMsg='Could not get your location. ';
            if(error.code==1) errorMsg+='Please allow location access.';
            else errorMsg+='Please search or click on the map.';
            document.getElementById('mapHint').innerHTML=errorMsg;
        });
    }else{
        alert('Geolocation not supported. Please search or click on the map.');
    }
}

async function load(){
    try{
        let r=await fetch('/api/incidents?t='+Date.now());
        let d=await r.json();
        updateMap(d);
        updateStats(d);
        updateList(d);
    }catch(e){}
}

function updateMap(incidents){
    markers.forEach(m=>map.removeLayer(m));
    markers=[];
    incidents.forEach(inc=>{
        let lat=inc.latitude||9.02497;
        let lng=inc.longitude||38.74689;
        let color=inc.severity=='Critical'?'#dc2626':(inc.severity=='Moderate'?'#f59e0b':'#1D9E75');
        let marker=L.marker([lat,lng]).addTo(map);
        marker.bindPopup('<b>'+inc.type+'</b><br>'+inc.location+'<br>Severity: '+inc.severity+'<br>Status: '+inc.status);
        markers.push(marker);
    });
}

function updateStats(incidents){
    document.getElementById('statTotal').innerText=incidents.length;
    document.getElementById('statCritical').innerText=incidents.filter(x=>x.severity=='Critical').length;
    document.getElementById('statResolved').innerText=incidents.filter(x=>x.status=='Resolved').length;
}

function updateList(incidents){
    let html='';
    incidents.forEach(inc=>{
        html+='<div class="incident-item"><b>#'+inc.id+'</b> '+inc.type+'<br>'+inc.location+'<br>'+inc.severity+' | '+inc.status+'<br>By: '+(inc.reporterName||'Anonymous')+'</div>';
    });
    document.getElementById('incidentList').innerHTML=html||'<p>No incidents reported</p>';
}

document.getElementById('incidentForm').addEventListener('submit',async(e)=>{
    e.preventDefault();
    if(!selectedLat){
        alert('Please select a location on the map first (click, search, or use my location)');
        return;
    }
    let data={
        type:document.getElementById('type').value,
        location:document.getElementById('location').value,
        severity:document.getElementById('severity').value,
        reporterName:document.getElementById('reporterName').value,
        description:document.getElementById('description').value,
        latitude:selectedLat,
        longitude:selectedLng
    };
    let s=document.getElementById('submitStatus');
    s.innerHTML='Submitting...';
    s.style.color='#666';
    try{
        let r=await fetch('/api/submit',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(data)});
        if(r.ok){
            s.innerHTML='Report submitted successfully!';
            s.style.color='#1D9E75';
            document.getElementById('incidentForm').reset();
            selectedLat=null;
            selectedLng=null;
            if(tempMarker) map.removeLayer(tempMarker);
            document.getElementById('mapHint').innerHTML='Search for a place or click on map';
            document.getElementById('mapHint').classList.remove('active');
            load();
            if(data.severity=='Critical') alert('CRITICAL INCIDENT! Emergency services notified.');
        }else{
            s.innerHTML='Error submitting report';
            s.style.color='#dc2626';
        }
    }catch(e){
        s.innerHTML='Network error. Please try again.';
        s.style.color='#dc2626';
    }
    setTimeout(()=>s.innerHTML='',3000);
});

document.getElementById('useMyLocationBtn').addEventListener('click', useMyLocation);

function switchTab(tab){
    document.querySelectorAll('.tab-btn').forEach(b=>b.classList.remove('active'));
    document.querySelectorAll('.tab-pane').forEach(p=>p.classList.remove('active'));
    if(tab=='report'){
        document.querySelectorAll('.tab-btn')[0].classList.add('active');
        document.getElementById('report-pane').classList.add('active');
    }else if(tab=='firstaid'){
        document.querySelectorAll('.tab-btn')[1].classList.add('active');
        document.getElementById('firstaid-pane').classList.add('active');
    }else{
        document.querySelectorAll('.tab-btn')[2].classList.add('active');
        document.getElementById('incidents-pane').classList.add('active');
    }
}

function toggleGuide(guide){
    document.getElementById(guide+'-steps').classList.toggle('show');
}

initMap();
</script>
</body>
</html>""";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.getResponseBody().close();
        }
    }

    static class ApiHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            List<Incident> incidents = dao.getAllIncidents();
            StringBuilder json = new StringBuilder("[");
            for(int i=0;i<incidents.size();i++){
                Incident inc = incidents.get(i);
                if(i>0) json.append(",");
                json.append("{\"id\":").append(inc.getId())
                    .append(",\"type\":\"").append(inc.getType().replace("\"","\\\""))
                    .append("\",\"location\":\"").append(inc.getLocation().replace("\"","\\\""))
                    .append("\",\"severity\":\"").append(inc.getSeverity())
                    .append("\",\"status\":\"").append(inc.getStatus())
                    .append("\",\"reporterName\":\"").append((inc.getReporterName()==null?"Anonymous":inc.getReporterName()).replace("\"","\\\""))
                    .append("\",\"latitude\":").append(inc.getLatitude() != null ? inc.getLatitude() : "null")
                    .append(",\"longitude\":").append(inc.getLongitude() != null ? inc.getLongitude() : "null")
                    .append("}");
            }
            json.append("]");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            exchange.getResponseBody().write(json.toString().getBytes());
            exchange.getResponseBody().close();
        }
    }

    static class SubmitHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if(!exchange.getRequestMethod().equals("POST")){exchange.sendResponseHeaders(405,-1);return;}
            BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder body = new StringBuilder();
            String line;
            while((line=br.readLine())!=null) body.append(line);
            String json = body.toString();
            String type = extract(json,"type");
            String location = extract(json,"location");
            String severity = extract(json,"severity");
            String reporterName = extract(json,"reporterName");
            String desc = extract(json,"description");
            String latStr = extract(json,"latitude");
            String lngStr = extract(json,"longitude");
            
            if(reporterName==null||reporterName.isEmpty()) reporterName="Anonymous";
            Incident inc = new Incident(type,location,severity,desc,reporterName);
            
            if(latStr!=null && !latStr.isEmpty() && !latStr.equals("null")){
                try{ inc.setLatitude(Double.parseDouble(latStr)); inc.setLongitude(Double.parseDouble(lngStr)); }catch(Exception e){}
            }
            
            boolean saved = dao.saveIncident(inc);
            if(saved && "Critical".equals(severity)) TrafficServer.broadcastEmergency(location,type);
            String resp = saved ? "SUCCESS" : "ERROR";
            exchange.sendResponseHeaders(saved?200:500, resp.getBytes().length);
            exchange.getResponseBody().write(resp.getBytes());
            exchange.getResponseBody().close();
        }
        private String extract(String json, String key){
            String s = "\""+key+"\":\"";
            int start = json.indexOf(s);
            if(start==-1){
                s = "\""+key+"\":";
                start = json.indexOf(s);
                if(start==-1) return "";
                start+=s.length();
                int end = json.indexOf(",",start);
                if(end==-1) end = json.indexOf("}",start);
                if(end==-1) return "";
                return json.substring(start,end).trim();
            }
            start+=s.length();
            int end = json.indexOf("\"",start);
            return end==-1 ? "" : json.substring(start,end);
        }
    }
}
