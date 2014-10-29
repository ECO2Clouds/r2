function [prediction] = co2prediction(location, powermin, powermax, initial_factor, month, dayofmonth, dayofweek ,hour, minute, duration, steps)

[en_mix] = readfactor(location);

prediction = [zeros(steps, 1), zeros(steps, 1)];
h = hour;
d = dayofmonth;
dw = dayofweek;
m = month;

dom = [31 28 31 30 31 30 31 31 30 31 30 31];

for ii=1:steps
    [a, b]=computeco2(en_mix, powermin, powermax, initial_factor, m, d, dw ,h, minute, duration);
    prediction(ii, 1) = a;
    prediction(ii, 2) = b;
    %[m d dw h minute]
    
    h = h + 1; 
    
    if (h == 24)
        d =  d + 1;
        dw = dw + 1;
        if (dw == 8)
            dw = 1;
        end
        if (d > dom(m))
            m = m+1;
            d = 1;
        end
        h = 1;
    end
       
end
end