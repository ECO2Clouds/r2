% return all the possible deployment combination for a set of vms
% in a set of sites
% vms Nx1 the set of vms to be deployed, the value indicates the number of
% cpus.
% locations NxM a set of sites admissible for the vm with the same index
% (e.g. vms(1) must be deployed in one of the sites in locations(1,:)


function comb = site_combinations(vms,locations)

    comb =  [];
    if length(vms)==1
        for ii=1:length(locations(1, :))
            if (locations(1, ii) > 0)
                comb = [comb; vms locations(1, ii)];
            end
        end
    else
        for jj=1:length(locations(1,:))
            A = [];
            X = [site_combinations(vms(2:end), locations(2:end, :))];        
            for ii=1:size(X, 1)        
                A = [A; vms(1) locations(1, jj)];
            end
            X = [A X];
            comb = [comb; X];
        end
    end

end