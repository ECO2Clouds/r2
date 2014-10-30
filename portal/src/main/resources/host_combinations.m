function comb = host_combinations(vms, hosts) 
    comb =  [];
    if length(vms)==1
        for ii=1:length(hosts)
            comb = [comb; vms hosts(ii)];
        end
    else
        for jj=1:length(hosts)
            A = [];
            X = [host_combinations(vms(2:end), hosts)];        
            for ii=1:size(X, 1)        
                A = [A; vms(1) hosts(jj)];
            end
            X = [A X];
            comb = [comb; X];
        end
    end

end