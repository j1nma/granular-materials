function kineticEnergy
	fid = fopen('./output/energy_file.txt');

	time = [0.0];
	energy = [0.0];

	# Read initial energy
	initialEnergy = fgetl(fid);
	[initialT initialE] = strsplit(initialEnergy(1:end), " "){1,:};
	initialT = str2num(initialT);
	initialE = str2num(initialE);

    # Read file
	while (!feof(fid))
    	    # Parse time-energy
    	    timeEnergy = fgetl(fid);
    	    [timeT energyT] = strsplit(timeEnergy(1:end), " "){1,:};
    	    time = [time, str2num(timeT)];
    	    energy = [energy, str2num(energyT)];
	endwhile

	fclose(fid);

    index = 0;
	#color='rmbcgyrmbc';
    markers = '.+*oxsd^v><ph';
    #props = {"color", color(index+1), "marker", markers(index+1), 'LineStyle', 'none'};
    props = {"marker", markers(index+1), 'LineStyle', 'none'};
    h = plot(time, energy);
    set(h, props{:})
    xlabel("Tiempo (s)", 'fontsize', 16);
    ylabel("Energía cinética (J)", 'fontsize', 16);
    #set(gca, 'fontsize', 18);
    axis([0 2])
    grid on

    hold all

	print("./output/kineticEnergy.png", "-dpngcairo", "-F:14")
end

