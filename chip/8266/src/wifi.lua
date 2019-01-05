mac_address=wifi.sta.getmac();
airkiss_id=string.upper(string.gsub(mac_address, ":", ""));
wifi.setmode(wifi.STATION)
wifi.eventmon.register(wifi.eventmon.STA_GOT_IP, function(T)
    log_print("GOT IP:"..T.IP)
    wifConnected=1;
    ip_address=T.IP;
    -- if(timer_started ~= 1) then
    --     timer_started=1;
    connect_server();
    --     tmr.start(0);
    --     start_airkiss();
    -- end
end)

wifi.eventmon.register(wifi.eventmon.STA_DISCONNECTED, function(T)
    log_print("Wifi disconnected")
    wifConnected=0;
    ip_address="";
end)

--wifi.eventmon.
wifi.sta.autoconnect(1)
ssid, password, bssid_set, bssid=wifi.sta.getconfig()
WIFI_SSID=ssid
if(ssid==nil or ssid=="") then
    log_print("Wait wifi config");
    wifi.startsmart(1,
        function(nssid, npassword)
            ssid=nssid;
            password=npassword;
            log_print(string.format("Success. SSID:%s ; PASSWORD:%s", ssid, password))
        end
    )
else
    log_print(string.format("Connect to wifi. SSID:%s", ssid))
    station_cfg={}
    station_cfg.ssid=ssid
    station_cfg.pwd=password
    station_cfg.save=false
    wifi.sta.config(station_cfg)
end