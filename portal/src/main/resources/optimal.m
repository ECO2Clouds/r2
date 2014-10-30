function [y] = optimal(no_vm,no_host)

    c = 1:no_vm
    c = [c zeros(1, no_vm-1)] % toglo la possibilit? di avere un deploy vuoto 

    comb = unique(combnk(c, no_vm), 'rows')
    no_comb = length(comb);

    % genera le combinazioni incompatibili
    inc = [];
    for ii=1:no_comb
        for jj=ii+1:no_comb
            i = intersect(comb(ii,:), comb(jj,:))
            if (length(i)>0 & length(find(i>0))>0)
                inc = [inc; ii jj]
            end
        end
    end
    no_inc = length(inc);
    
    f = no_comb*no_host:-1:1 % valori da computare
    no_var = length(f);
    
    i
    ntcon=1:no_comb*no_host % variabili intere ogni combinazione su tutti gli host
    %intcon=[]
    % combinazioni incompatibili non possono essere ospitate dallo stesso
    % host, ma una tra loro DEVE essere ospitata
    

    Aeq = [];
    for ii=1:size(inc, 1)
        Y = zeros(1, no_comb)
        Y(inc(ii,:)) = 1;
        X =  [];
        for jj = 1:no_host
            X = [X Y];
        end
        Aeq = [Aeq; X]
    end
    
    beq = ones(no_inc,1)
    
    lb = zeros(no_var, 1)
    ub = ones(no_var,1)
    
    A = []
    b = []
    
    min_sol = intlinprog(f, intcon, Aeq, beq, A, b, lb, ub)
    -f
    max_sol = intlinprog(-f, intcon, Aeq, beq, A, b, lb, ub)
    
    for ii=0:no_host-1
    %    var = y((no_comb*ii)+1:no_comb*(ii+1))
    end
end
