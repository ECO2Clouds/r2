function [power_min, power_max, comb_min, comb_max] = computepowersite(vms, hosts, current_cpuload, location) 
   


    no_host = length(hosts);

    res = host_combinations(vms, hosts);
    no_comb = size(res, 1);
    
    power = zeros(no_comb, no_host);
    
    for ii=1:no_comb
        for jj=1:no_host
              no_cpu = 0;
              for kk=2:2:length(res(ii,:))
                  if res(ii, kk) == hosts(jj) 
                    no_cpu = no_cpu + res(ii,kk-1);
                  end
              end
            power(ii, jj) = computepower(no_cpu, hosts(jj), current_cpuload, location); 
        end
    end

    comb = [res power sum(power')']
    
    [m i] = min(sum(power')');
    
    power_min = m;
    
    comb_min = [res(i,:) m];

    [m i] = max(sum(power')');
    
    power_max = m;
    
    comb_max = [res(i, :) m];
    
end