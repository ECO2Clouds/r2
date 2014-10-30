%cpus number of cpus to be deployed on host
%host on which the cpu will be deployed
%current_cpuload for each host [0..100%] 
%location 1=fr-inria 2=uk-epcc 3=de-hlrs
function p = computepower(cpus, host, current_cpuload, location)

%supponiamo inria quindi host non ? importante per il calcolo
no_hostINRIA = 4;
no_hostEPCC = 7;
no_hostHLRS = 35;

no_cpuINRIA = [24 24 24 24];
no_cpuEPCC = [48 48 16 16 16 16 16];
no_cpuHLRS = [4 4 4 4 4 48 48 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8 8];

if (location == 1)
    no_host = no_hostINRIA;
    no_cpu = no_cpuINRIA;
end
if (location == 2)
    no_host = no_hostEPCC;
    no_cpu = no_cpuEPCC;
end
if (location == 3)
    no_host = no_hostHLRS;
    no_cpu = no_cpuHLRS;
end

current_cpuload = current_cpuload./100; % change the range from 0..1

% inizializzo i valori per utti  gli host
currentpower = zeros(1, no_host);
for ii=1:no_host
    c = current_cpuload(ii);
    
    if (location == 1) % at inria all the hosts have the same power model
        pnow = -61.64*c*c + 131*c + 139.3;
    end
    if (location == 2)
        if (ii <= 2) % vmhost0 & vmhost1
            pnow = -156.2*c*c + 439*c + 364.3;
        else % vmhost2 to vmhost6
            pnow = -113*c*c + 208.9*c + 106.4;
        end
    end
    if (location == 3)
        if (ii<=6) %floccus06
            pnow = -7.08*c*c + 101.3*c + 154.8;
        end
        if (ii==7 || ii==8) %floccus16
            pnow = -156.2*c*c + 439*c + 364.3;
        end
        if (ii>=9) % node0104
            pnow = -8.896*c*c + 114.3*c + 155.3;
        end
    end
    currentpower(ii) = pnow;
end

cnew = current_cpuload(host) + cpus/no_cpu(host); % le cpu sono al massimo
% si calcola con formula diversa a seconda dell'host
if (location == 1) % at inria all the hosts have the same power model
    pnew = -61.64*cnew*cnew + 131*cnew + 139.3;
end
if (location == 2)
    if (host <= 2) % vmhost0 & vmhost1
        pnew = -156.2*cnew*cnew + 439*cnew + 364.3;
    else % vmhost2 to vmhost6
        pnew = -113*cnew*cnew + 208.9*cnew + 106.4;
    end
end
if (location == 3)
    if (host<=6) %floccus06
        pnew= -7.08*cnew*cnew + 101.3*cnew + 154.8;
    end
    if (host==7 || host==8) %floccus16
        pnew = -156.2*cnew*cnew + 439*cnew + 364.3;
    end
    if (host>=9) % node0104
        pnew = -8.896*cnew*cnew + 114.3*cnew + 155.3;
    end
end
p = pnew - currentpower(host);

end