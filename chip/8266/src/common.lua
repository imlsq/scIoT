function my_string_trim(s)
    return (s:gsub("^%s*(.-)%s*$", "%1"))
 end

 function binary_array_to_hex_string(arry,len)
    ds=""
    for i=1,len do
        ts=string.format('%X',arry[i])
        if(string.len(ts)<2) then
            ts="0"..ts
        end
        ds=ds..ts
    end
    --print(ds);
    return ds;
end

function log_print(string)
    --if(gpio.read(ENABLE_UART_GPIO)==0) then
        print(string);
    --end
end

function write_uart(string)
    print(string);
end