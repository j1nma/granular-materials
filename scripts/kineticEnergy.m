function kineticEnergy
	fid = fopen('./output/energy_file.txt');

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

    props = {"marker", '.', 'LineStyle', 'none'};
    h = plot(time, log10(energy));
    set(h, props{:})
    xlabel("Tiempo (s)");
    ylabel("log_{10}(Energía cinética) (J)");
    set(gca, "yticklabel", num2str(get(gca, "ytick"), '%.e|'))
    axis([0 time(end)])
    grid on

    hold all

	print(sprintf("./output/kineticEnergy-N=%d-T=%ds-Log10.png", N, round(time(end))), "-dpngcairo", "-F:14")
end

