hosts = unique(hoststatus(:,1));

final = unique(hoststatus(:,2));
for ii=hosts'

    status_host = hoststatus(find(hoststatus(:,1)==ii), 3);
    final = [final status_host];
    %[ii size(find(hoststatus(:,1)==ii))]
end

plot(final(1), final(2:end))