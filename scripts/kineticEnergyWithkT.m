function kineticEnergyWithkT(kt, dirName)
	fid = fopen(sprintf("./output/energy_file_D=0.0_kT=%d.0.txt", kt));

	time = [0.0];
	energy = [0.0];

	# Read N
    initialLine = fgetl(fid);
    [N] = strsplit(initialLine(1:end), " "){1,:};
    N = str2num(N);

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

    jump = find(energy(2:end) < 1e-18, 1, 'first');

    props = {'marker', '.', 'LineStyle', 'none'};
    h = plot(time, log10(energy), sprintf(";kT = %dN/m;", kt));
    set(h, props{:})
    xlabel("Tiempo [s]");
    ylabel("log_{10}(Energía cinética) [J]");
    #set(gca, "xtick", time(jump + 1))
    grid on
    legend("location", "northeast");

    hold all

	print(sprintf("%s/kineticEnergy-N=%d-T=%ds-D=0.0-kT=%d-Log10.png", dirName, N, round(time(end)), kt), "-dpngcairo", "-F:14")
end

