function [en_mix] = readfactor(location)

if (location == 1)
    en_mix = importEnergyMix('/Users/plebani/Documents/MATLAB/powerINRIA.csv',2);
end
if (location == 2)
    en_mix = importEnergyMix('/Users/plebani/Documents/MATLAB/powerEPCC.csv',2);
end
if (location == 3)
    en_mix = importEnergyMix('/Users/plebani/Documents/MATLAB/powerHLRS.csv',2);
end

end