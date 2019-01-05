dofile("variable.lua")
dofile("common.lua")
dofile("iot_server.lua")
dofile("wifi.lua")

gpio.write(ENABLE_UART_GPIO, gpio.HIGH)
tmr.register(0,20000,tmr.ALARM_AUTO,function()
    if(wifConnected==1) then
        connect_server()
        hearbeat()
    end

    if(iot_socket~=nil and not_received_hb_msg_count>3) then
        --3次没有收到服务器的回复，就重置socket
        close_socket();
    end

end)
tmr.start(0);