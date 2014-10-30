function [co2min, co2max]=computeco2site(location, powermin, powermax, initial_factor, month, dayofmonth, dayofweek ,hour, minute, duration)

    [en_mix] = readfactor(location);

    [co2min, co2max]=computeco2(en_mix, powermin, powermax, initial_factor, month, dayofmonth, dayofweek ,hour, minute, duration);
end
