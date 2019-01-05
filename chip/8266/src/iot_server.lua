server_gd="";
function connect_server()
    if(iot_server_domain ==nil or iot_server_domain =="") then
        return;
    end

    if(iot_socket==nil and iot_server_domain ~=nil and iot_server_port ~="") then
        log_print("Connect to server,s="..iot_server_domain.." p="..iot_server_port)
        iot_socket=tls.createConnection()
        iot_socket:on("receive", function(c,pl)
            --cmdHandler(c, pl)
            not_received_hb_msg_count=0;
            append_data(c,pl);
        end)
        iot_socket:on("connection", function(sck, c)
            iot_connected=1;
            send_message_to_server_over_socket("AT+LOGIN=8266|"..version.."|"..device_id.."|"..airkiss_id.."|"..reset_version.."\r\n")
        end)
        iot_socket:connect(iot_server_port,iot_server_domain)
    end
end

function close_socket() 
    if(iot_socket ~=nil) then
        iot_socket:close();
        iot_socket=nil;
        iot_connected=0;
        not_received_hb_msg_count=0;
        log_print("Socket is disconnection")
    end
end


function append_data(socket,data)
    --log_print(data)
    data_len=0;
    if(data==nil) then
         return;
    end
    data_len=string.len(data)
    local b_at="";
    for i=1, data_len do
        b_at=string.sub(data,i,i);
        if(b_at=="\n") then
            cmdHandler(socket,string.sub(server_gd,1,i));
            server_gd="";
        else
            server_gd=server_gd..b_at;
            if(string.len(server_gd)>512) then
                server_gd="";
            end
        end
    end
end

function hearbeat()
    if(iot_connected==1 and iot_socket~=nil) then
        send_message_to_server_over_socket("hb\n");
        not_received_hb_msg_count=not_received_hb_msg_count+1;
    end
end

function cmdHandler(c, pl)
    local tmp_s="";
    pl=string.gsub(pl, "\r", "")
    log_print("[rev]"..pl)
    if (string.match(pl,"AT%+TIME=")~=nil) then
        local tmp_s=string.gsub(pl, "AT%+TIME=", "")
        rtctime.set(tmp_s/1000, 0)
        sync_time=1
        send_message_to_server_over_socket("+TIME:OK\r\n");
    elseif (string.match(pl,"AT%+U1HD=")~=nil) then
        tmp_s=string.gsub(pl, "AT%+U1HD=", "")
        tmp_s=string.gsub(tmp_s, "\n", "")
        tmp_s=string.gsub(tmp_s, "\r", "")
        local s_index=1;
        while(s_index<string.len(tmp_s)-1)
        do
            --log_print(s_index..","..string.sub(tmp_s,s_index,s_index+1));
            uart.write(0,tonumber(string.sub(tmp_s,s_index,s_index+1),16))
            s_index=s_index+2;
        end
        send_message_to_server_over_socket("+U1HD:OK\r\n");
    end
end

function send_message_to_server_over_socket(msg)
    if(iot_connected==1 and iot_socket~=nil) then
        iot_socket:send(msg);
    end
end
