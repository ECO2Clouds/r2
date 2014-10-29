%location 1=fr-inria 2=uk-epcc 3=de-hlrs
%the amount of power used in the location
%the current month
%the current dayofweek 1=monday 7=sunday
%the current hour
%the current minute
%duration of the experimt in mins

function [co2min, co2max]=computeco2(en_mix, powermin, powermax, initial_factor, month, dayofmonth, dayofweek ,hour, minute, duration)

start = find(en_mix(:, 1)==month & en_mix(:, 2) == 0 & en_mix(:, 3)==0);
emissionWD = zeros(48, 1);
emissionWE = zeros(48, 1);
for ii=1:48
    emissionWD(ii) = en_mix(start, 5);
    emissionWE(ii) = en_mix(start, 6);
    start = start+1;
end

start = find(en_mix(:, 1)==month+1 & en_mix(:, 2) == 0 & en_mix(:, 3)==0);
emissionWD1 = zeros(48, 1);
emissionWE1 = zeros(48, 1);
for ii=1:48
    emissionWD1(ii) = en_mix(start, 5);
    emissionWE1(ii) = en_mix(start, 6);
    start = start+1;
end

start = hour * 2;
if (minute == 30)
    start = start +1;
end

emissionWD = circshift(emissionWD, -start);
emissionWE = circshift(emissionWE, -start);
emissionWD1 = circshift(emissionWD1, -start);
emissionWE1 = circshift(emissionWE1, -start);

co2= initial_factor;
nextmonth = 0;

steps = floor((duration-1)./30)+1;
jj = 1;

for ii=1:steps %30 days
    if nextmonth==0
        if dayofweek==6 || dayofweek==7
            co2 = co2*emissionWE(jj)./100;
        else
            co2 = co2*emissionWD(jj)./100;
        end
    else
        if dayofweek==6 || dayofweek==7
            co2 = co2*emissionWE1(jj)./100;
        else
            co2 = co2*emissionWD1(jj)./100;
        end
    end
    if (jj==48)
        jj = 1;
        if (dayofweek == 7)
            dayofweek=1;
        else
            dayofweek = dayofweek+1;
        end
        
        if (dayofmonth == 30)
            nextmonth = 1;
            dayofmonth = 1;
        else
            dayofmonth = dayofmonth+1;
        end
    else
        jj = jj+1;
    end
end

co2min = sum(co2'*powermin)/60; % divided by 60 because factor is expressed in gr/Wh
co2max = sum(co2'*powermax)/60; % divided by 60 because factor is expressed in gr/Whend
end