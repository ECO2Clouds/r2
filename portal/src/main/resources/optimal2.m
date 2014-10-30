function [y] = optimal2(no_vm,no_host, f)

    c = 1:no_vm
    c = [c zeros(1, no_vm)]
    
    comb = unique(combnk(c, no_vm), 'rows')
    no_comb = length(comb);

    H = []; % per ogni host tutte le combinazioni
    for ii=1:no_host
        h = ones(no_comb, 1)*ii;
        H=[H; h comb];
    end
    H
    
    f = []
    for ii=1:no_vm
        for jj=1:no_host
            for kk=1:no_comb
                cpu = length(find(H(kk,2:end)>0));
                f = [f computepower(1+cpu, jj)]
            end
        end
    end
    % f = no_comb*no_host*no_vm:-1:1 % valori da computare
    no_var = length(f);
    
    intcon=1:no_comb*no_host*no_vm % variabili intere ogni combinazione su tutti gli host

    % una vm deve trovare uno e un solo host
    Aeq = [];
    X=ones(1, no_comb*no_host);
    X =  [X zeros(1, no_comb*no_host*(no_vm-1))];
    Aeq = [Aeq; X];
    for ii=1:no_vm-1
        X = circshift(X, no_comb*no_host, 2);
        Aeq = [Aeq; X];
    end
    
    beq = ones(no_vm,1);
    
    % elimina possibilit? di avere una vm in cui ? gi? prevista
    for ii=1:no_vm
        X = [];
        [r, c] = find(H(:, 2:end)==ii);
        Y = zeros(1, no_comb*no_host);
        if (length(r') > 0)
            Y(r) = 1;
        end
        for jj=1:no_host
            X = [X Y];
        end
        Aeq =  [Aeq; X];
        beq = [beq; 0];
    end
    Aeq
    beq
    
    lb = zeros(no_var, 1)
    ub = ones(no_var,1)
    
    A = [];
    b = [];
    
    min_sol = intlinprog(f, intcon, A, b, Aeq, beq, lb, ub)
    max_sol = intlinprog(-f, intcon, A, b, Aeq, beq, lb, ub)

    H;
    minimum = reshape(min_sol, 8, 2);
    maximum = reshape(max_sol, 8, 2);

    [H minimum maximum]
    
    
end
