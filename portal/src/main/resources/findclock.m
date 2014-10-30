int_power = [];
for i=1:size(powerinv)
    int_power = [int_power; trapz(powerinv(1:i))];
end

int_cpuload = [];
for i=1:size(cpuload_rand)
    int_cpuload = [int_cpuload; trapz(cpuload_rand(1:i))];
end