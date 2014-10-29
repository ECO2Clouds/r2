!				-----------------------------
!				    Marcello Schiavina
!				Master thesis October 2008 ;)
!				-----------------------------

module eels

	use distributions
	implicit none
	
	integer,parameter	:: rp		= SELECTED_REAL_KIND(12,37)		!parametri pre kind di real e int
	integer,parameter	:: rp_low	= SELECTED_REAL_KIND(5,5)		!parametri pre kind di real e int
	integer,parameter	:: ip		= SELECTED_INT_KIND(9)

	integer(ip),parameter	:: Nnode_x		= 202			!Numero di nodi
	integer(ip),parameter	:: Nnode_y		= 157
	integer(ip),parameter	:: Nnode_z		= 22
	integer(ip),parameter	:: files_x_year	= 74
	!integer(ip),parameter	:: McCvert		= 10			!Vertici del Poligono di McCleave
	real(rp),parameter	:: length_x		= 100.5_rp		!Ampiezza del dominio [∞]
	real(rp),parameter	:: length_y		= 78._rp		![∞]
	real(rp),parameter	:: length_z		= 918.37_rp		![m]
	real(rp),parameter	:: R			= 6378137._rp		!Raggio medio terr [m]
	real(rp),parameter	:: pi			= 3.1415926535_rp	!Pi Greco 
	real(rp),parameter	:: pi180		= pi/180._rp 
	real(rp),parameter	:: invpi180		= 1._rp/pi180 
	real(rp),parameter	:: piquarti		= pi/4._rp
	real(rp),parameter	:: day_x_month	= 365._rp/12._rp		!giorni al mese
	!real(rp),parameter	:: a_density	= 49.2			!densit‡ per mortalit‡
	real(rp),parameter	:: Act_energy	= 1.23_rp		![eV] energia di attivazione
	real(rp),parameter	:: Boltzmann	= 8.62e-5		![eV/K] costante di Boltzmann
	!real(rp),dimension(10,2),parameter :: McCpoly = (/ [-74._rp, -69._rp, -63._rp, -57._rp, &
	!	-50._rp, -52.5_rp, -67._rp, -66.5_rp, -67.5_rp, -70._rp], [24._rp, 30._rp, 28.5_rp, &
	!	30._rp, 26.5_rp, 24._rp, 23._rp, 24._rp, 24.5_rp, 23.5_rp] /)
	integer(ip),parameter	:: months_x_year	= 12
	integer(ip),dimension(12,2),parameter	:: days_x_month	= RESHAPE(SOURCE = (/ [31,28,31,30,&
		31,30,31,31,30,31,30,31],[31,28,31,30,31,30,31,31,30,31,30,31] /), SHAPE=(/12,2/))

	!eels_pos(id,info) info: 1 lambda 2 phi 3 z
	!
	!eels_data(id,info) info: 1 et‡ in passi
	!2 lunghezza [mm] 3 lunghezza al passo precedente
	!4 passo di nascita  5 growth_factor
	!
	!eels_int(id,info) info: 1 flag per i transetti (-1 fuori dal dominio) 
	!2 Nodo y precedente 3 count per temperature 4 count per salinit‡
	!
	!(int((id-1)/Neeltot)+1) --> restituisce la coorte di appartenenza
	!(mod((id-1),Neeltot)+1) --> mi dice la posizione relativa nella coorte
	real(rp),dimension(:,:),allocatable			:: eels_pos
	real(rp),dimension(:,:),allocatable			:: eels_data
	real(rp_low),dimension(:,:,:),allocatable		:: eels_tracking!coordinates of larvae
	real(rp_low),dimension(:,:,:),allocatable		:: eels_tracking_final!coordinates of successful larvae
 	real(rp),dimension(:,:,:,:),allocatable			:: ocean_speed	!ocean_speed(nx,ny,nz,info) info [cm/s]: 1-velx 2-vely 3-nextvelx 4-nextvely 5-act velx 6-act vely
 	real(rp_low),dimension(:,:,:,:),allocatable		:: ocean_temp	!ocean_temp(nx,ny,nz,info) info [∞C]: 1-temp 2-nexttemp 3-actual temp
 	real(rp_low),dimension(:,:,:,:),allocatable		:: ocean_salt	!ocean_salt(nx,ny,nz,info) info [mg/l?]: 1-salt 2-nextsalt 3-actual salt
	real(rp),dimension(:,:,:,:),allocatable			:: ocean_coord	!ocean_coord(nx,ny,nz,info) coordinate geografiche info: 1 lambda 2 phi 3 z
	real(rp_low),dimension(:,:),allocatable			:: isolength	!isolength(nx,ny) min length in each "node" of the grid
	!real(rp_low),dimension(:,:,:,:),allocatable		:: meanlength
!	real(rp),dimension(:),allocatable				:: lambda		!tassi di crescita per ogni passo e inverso per i calcoli di sopravvivenza
!	real(rp),dimension(:),allocatable				:: invlambda
	integer(ip),dimension(:,:),allocatable			:: eels_int
	integer(ip),dimension(:),allocatable			:: g_month		!numero giorni dei mesi
	integer(ip),dimension(:),allocatable			:: births_x_day	!nascite al giorno
	integer(ip),dimension(:),allocatable			:: births_x_year!contatore delle nascite annuali
	character(len=14),dimension(:),allocatable 		:: speedfiles 	!nomi dei file da caricare per le velocit‡
	character(len=14),dimension(:),allocatable 		:: tempfiles 	!nomi dei file da caricare per le temperature
	character(len=14),dimension(:),allocatable 		:: saltfiles 	!nomi dei file da caricare per la salinit‡

	real(rp) :: delta_x				!spaziatura dei nodi
	real(rp) :: delta_y
	real(rp) :: delta_z
!	real(rp) :: lambda1				!tasso di accrescimento giornaliero [mm/gg] 0-2 mesi
!	real(rp) :: lambda2				!tasso di accrescimento giornaliero [mm/gg] 2-8 mesi
!	real(rp) :: lambda3				!tasso di accrescimento giornaliero [mm/gg] 8-oo mesi
	real(rp) :: a_mortality
!	real(rp) :: delta_mortality
	real(rp) :: gamma_mortality
	real(rp) :: step_x_day			!passi al giorno
	real(rp) :: day_x_step			!inverso dei passi al giorno
	real(rp) :: month_x_day			!inverso dei giorni al mese
	real(rp) :: day,tempvel			!variabili di ottimizzazione
	integer(ip) 	:: update				!variabile per gestire l'aggiornamento dei dati oceanici
	integer(ip) 	:: month,year,months			!data della simulazione
	integer(ip)		:: bises				! 1- if it's a non bisestile year 2- if it is
	integer(ip) 	:: Neeltot	 			!numero di rilasci per coorte
	integer(ip) 	:: up_z				!variabile per l'aggiornamento della coordinata z
	integer(ip) 	:: births				!contatore delle nascite
!	integer(ip) KH 					!booleano per le simulazioni K&H	


	!namelist init.dat
	integer(ip) 	:: time_killer		!tempo di vita delle larve
	integer(ip) 	:: time				!durata in anni della simulazione
	integer(ip) 	:: delta_t			!delta di aggiornamento dei dati di velocit√†
!	integer(ip) 	:: growth			!tipologia di crescita
	integer(ip) 	:: Neel				!Numero di anguille per layer
	integer(ip) 	:: year_start		!anno della prima nascita di larve
	integer(ip) 	:: year_stop		!anno termine di nascite
	integer(ip) 	:: month_start		!mese di inizio rilasci 
	integer(ip) 	:: month_stop		!mese termine rilasci
	integer(ip) 	:: idum				!seed per le estrazioni di numeri pseudorandom
	real(rp) 	:: preboundary			!preFinish line
	real(rp) 	:: boundary			!Finish line
	real(rp) 	:: delta_birth			!intervallo di passi tra i rilasci
	real(rp) 	:: step_h				!ampiezza in ore del passo temporale	
	real(rp) 	:: BL_s				!capacit‡ di nuoto [BodyLength/s]
	real(rp) 	:: threshold_length	!taglia minima da cui inizia il moto attivo ed il buoyancy control [mm]
	real(rp)	:: birth_length		!taglia media alla nascita
	real(rp) 	:: sigma_gf			!deviazione standard del growth_factor
	real(rp) 	:: depth_start			!limite profondit√† superiore dei rilasci
	real(rp) 	:: depth_stop			!limite profondit√† inferiore dei rilasci	
	real(rp) 	:: delta_depth			!intervallo di profondit√† dei rilasci (-1 per sim su ogni layer)
	real(rp) 	:: mu_night_start		!Variabili per diel vertical migration
	real(rp) 	:: dmu_night			!(Vedi guida o init_readme)
	real(rp) 	:: sigma_night_start
	real(rp) 	:: dsigma_night
	real(rp) 	:: mu_day_start
	real(rp) 	:: dmu_day
	real(rp) 	:: sigma_day_start
	real(rp) 	:: dsigma_day
	real(rp) 	:: mu_birth
	real(rp) 	:: sigma_birth
	real(rp) 	:: up_lim_birth
	real(rp) 	:: down_lim_birth
	real(rp)	:: alpha_nav			!coeff di navigazione y/x
	real(rp) 	:: a_density			!tasso di mortalit‡ annuo per unit‡ di peso mu=a*W^b
	real(rp) 	:: b_mortality			!esponente mortalit‡-peso
	real(rp) 	:: alpha_weight		!peso per unit‡ di volume W=alpha*L^beta
	real(rp) 	:: beta_weight			!esponente volume-lunghezza
	real(rp) 	:: sigma_time_birth	!deviazione standard delle nascite nel tempo [mesi]
	real(rp) 	:: mu_lambda			!Variabili per la distribuzione gauss bivariata dell'ellissoide di nascita
	real(rp) 	:: sigma_lambda
	real(rp) 	:: mu_phi
	real(rp) 	:: sigma_phi
	real(rp) 	:: L_0					!Taglia alla nascita
	real(rp) 	:: alpha_growth		!coeff power law
	real(rp) 	:: beta_growth			!esponente power law

	

	namelist /init/ preboundary, boundary, step_h, &
		delta_depth, time, Neel, delta_birth, time_killer, & ! growth, &
		year_start, year_stop, depth_start, depth_stop, &
		month_start, month_stop, BL_s, mu_night_start, dmu_night, sigma_night_start, &
		dsigma_night, mu_day_start, dmu_day, sigma_day_start, dsigma_day, &
		mu_birth, sigma_birth, up_lim_birth, down_lim_birth, alpha_nav, &
		a_density, b_mortality, alpha_weight, beta_weight, threshold_length, &
		birth_length, sigma_gf, idum, sigma_time_birth, mu_lambda, sigma_lambda, &
		mu_phi, sigma_phi, L_0, alpha_growth, beta_growth

	contains


!Inizializza il sistema allocando le variabili del modulo
!e lanciando le funzioni per caricare il campo e le anguille iniziali

	subroutine initialize(step,stepup,counter_larvae)

		integer(ip),intent(inout) :: step,stepup
		integer(ip),dimension(2),intent(inout) :: counter_larvae
		real(rp) temp
		integer(ip) i,h,temp2
		
		open(101,file="data/init.dat",access='sequential')
		read(101,NML=init)
		close(101)

! ciclo random number per ottenere simulazioni diverse
		i=0
		do while(i<idum)
			call random_number(temp)
			i=i+1
		enddo


!delta_x √® l'unico intervallo costante (intervallo di longitudine in gradi)
		delta_x = 0.5_rp

		day_x_step=step_h/24._rp
		step_x_day=24._rp/step_h
		month_x_day=1._rp/day_x_month
		months = time*12+1



! trasformo il coeff di brody da month^-1 a step^-1
		!brody=brody/day_x_month*day_x_step

!trasformo i tassi di crescita giornalieri in tassi di crescita per passo
!mettendoli in un array. La posizione dell'array indica l'et√† della larva
!in mesi per eccesso. il primo lambda1 √® relativo al periodo 0-3 mesi
!il secondo 3-8 mesi il terzo per tutto il resto
		
!		growth=growth+2
!		
!		open(101,file="data/growthrate.dat",access='sequential')
!		do i=1, 3
!			if (i==growth) then
!				read(101,*) lambda1, lambda2, lambda3
!				exit
!			else
!				read(101,*) temp,temp,temp
!			endif
!		enddo
!		close(101)
!		
!	   	allocate(lambda(time_killer))
!		allocate(invlambda(time_killer))
!		do i=1, time_killer
!			if (i<(3*day_x_month*step_x_day+1)) then
!				lambda(i)=lambda1*day_x_step
!				invlambda(i)=1/lambda1
!			elseif (i<(8*day_x_month*step_x_day+1)) then
!				lambda(i)=lambda2*day_x_step
!				invlambda(i)=1/lambda2
!			else
!				lambda(i)=lambda3*day_x_step
!				invlambda(i)=1/lambda3
!			endif
!		enddo

		allocate(speedfiles(2*files_x_year*time+4))
		allocate(tempfiles(2*files_x_year*time+4))
		allocate(saltfiles(2*files_x_year*time+4))
		allocate(g_month(2*files_x_year*time+4))
		allocate(isolength(Nnode_x,Nnode_y))
		!allocate(meanlength(Nnode_x,Nnode_y,months,2))

!Trasformo la durata della simulazione, definita in anni, in passi
!tenendo conto degli anni bisestili
		do i=0 , time-1
			if (mod(year_start+i,4)==0) then
				temp=temp+(366*step_x_day)
			else
				temp=temp+(365*step_x_day)
			endif
		enddo 
		time=temp

!Inizializzo le variabili di loop e i contatori di anguille per
!definire la finestra mobile nell'array di anguille su cui lavorare
		step=1
		stepup=step
		counter_larvae(1)=0	!--> all'inizio non ho coorti
		counter_larvae(2)=0	!--> 
		update=3
		day = 0			!Inizializzo a 0 poi modOcean assegna il valore corretto
		month = 0		!Idem
		year = year_start
		up_z=0
		births=0
		allocate(births_x_year(year_stop-year_start))
	
		do h=1, (year_stop-year_start)
			births_x_year(h) = 0
			do i=month_start, month_stop-1
				if (i==1) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==2) then
					if (mod((year_start+h-1),4)==0) then
						births = births + 29
						births_x_year(h) = births_x_year(h) + 29
					else
						births = births + 28
						births_x_year(h) = births_x_year(h) + 28
					endif
				elseif (i==3) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==4) then
					births = births + 30
					births_x_year(h) = births_x_year(h) + 30
				elseif (i==5) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==6) then
					births = births + 30
					births_x_year(h) = births_x_year(h) + 30
				elseif (i==7) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==8) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==9) then
					births = births + 30
					births_x_year(h) = births_x_year(h) + 30
				elseif (i==10) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				elseif (i==11) then
					births = births + 30
					births_x_year(h) = births_x_year(h) + 30
				elseif (i==12) then
					births = births + 31
					births_x_year(h) = births_x_year(h) + 31
				endif
			enddo
		enddo

		allocate(births_x_day(births))

!Estraggo la data di nascita per ciascuna larva cosi da sapere quante nascite ci sono per ogni coorte
		
		do i=1 , births
			births_x_day(i)=0
		enddo
		
		temp2=0
		do h=1 , (year_stop-year_start)
			do i=1 , Neel
				call gauss_limited(temp,int(((month_stop-month_start)* &
					day_x_month)/(delta_birth*2))+1._rp,int(sigma_time_birth*day_x_month/delta_birth)+0._rp, &
					1._rp,births_x_year(h)+0._rp)
					
				births_x_day(int(temp) + temp2) = births_x_day(int(temp) + temp2) + 1
			enddo
			temp2 = temp2 + births_x_year(h)
		enddo

!Inizializzo il contatore delle nascite a 1
		births = 1

!Trasformo delta_birth da giorni a passi
		if (delta_birth/=-1) then
			delta_birth=delta_birth*step_x_day
		endif


!	alloco lo spazio per l'oceano e per le anguille
		allocate(ocean_speed(Nnode_x,Nnode_y,Nnode_z,6)) 
		allocate(ocean_coord(Nnode_x,Nnode_y,Nnode_z,3))
		allocate(ocean_temp(Nnode_x,Nnode_y,Nnode_z,3))
		allocate(ocean_salt(Nnode_x,Nnode_y,Nnode_z,3))

!	carico le velocit√† dell'oceano
		call modOcean(.true.)

!	definisco alcune variabili di calcoli ripetuti nei cicli
		tempvel = step_h*3600
		Neeltot = Neel*(year_stop-year_start)
		
!	parametri per il calcolo della mortalit‡
		!delta=beta*b+1
		!delta_mortality=beta_weight*b_mortality+1
		!trasformo il tasso annuo di motalit‡ per unit‡ di peso in tasso giornaliero
		a_mortality=exp(a_density)*day_x_step/365
		!gamma=-a*alpha/delta

!	se la navigazione Ë attiva modifico alpha_nav = arctan(b/a)
		if (alpha_nav>=0) then
			alpha_nav=atan(alpha_nav)
		endif

!	alloco lo spazio per le anguille
		allocate(eels_pos((Neeltot),3))
		allocate(eels_data((Neeltot),7))
		allocate(eels_int((Neeltot),4))
		allocate(eels_tracking((Neeltot),time_killer*12+1,3))

!trasformo il tempo di vita delle larve da anni a passi
		time_killer=time_killer*365*step_x_day

	end subroutine initialize

!memorizza le coordinate dei nodi dell'oceano
	subroutine OceanCoord

		real(rp),dimension(3) :: coord
		integer(ip) h,k,f
		integer(ip),dimension(293,2) :: temp

! coordinate di y e z da file (in x hanno delta costante)
		open(77,file="/media/OceanData/coordinate_y.dat",access='sequential')
		open(78,file="/media/OceanData/coordinate_z.dat",access='sequential')
		coord(1)=-100.25
		do h=1, Nnode_x
			do k=1, Nnode_y
				read(77,*) coord(2)
! inizializzo la mesh delle isolunghezze con un numero elevato
				isolength(h,k)=10000
				do f=1, Nnode_z
					read(78,*) coord(3)
					ocean_coord(h,k,f,1)=coord(1)
					ocean_coord(h,k,f,2)=coord(2)
					ocean_coord(h,k,f,3)=coord(3)
				end do
				rewind 78
				
			enddo
			rewind 77
			coord(1)=coord(1)+0.5
		enddo

		close(77)
		close(78)

	end subroutine OceanCoord

!Carico le velocit√† dell'oceano.
!Se √® la prima volta (first==true) carico il mese
!attuale e quello sucessivo; se non √® la prima volta
!carico solo il mese successivo (l'attuale √® il successivo
!del caricamento precedente)
	subroutine modOcean(first)

		logical,intent(in) :: first
		!integer(ip),intent(in) :: step
		!character filename
		integer(ip) h,k,f,j,flag,year_temp,temp2, temp_month, temp_year, temp_bises
		character(len=11) temp
		character(len=5) filename, filename2
		character(len=170) command
		 
			
		if (first) then

!	assegno le coordinate ai nodi
			call OceanCoord

!	carico i nomi dei file da aprire solo per l'intervallo di durata della simulazione
			open(111,file="/media/OceanData/speed.dat", access='sequential')
			open(121,file="/media/OceanData/temp.dat", access='sequential')
			open(131,file="/media/OceanData/salt.dat", access='sequential')
			open(141,file="/media/OceanData/date.dat", access='sequential')
			if (year_start/=58) then
				h=1
				year_temp=1900+year_start
				year = 1958
				month = 1
				day = 1
				do while (((year<year_temp-1).or.(month<12)).or.(day<22))
					read(141,*) year, month, day
					read(111,*) temp,temp,temp
					read(121,*) temp
					read(131,*) temp
				enddo
			endif
			read(141,*) year, month, day
			if (mod(year,4) == 0) then
				bises = 2
			else
				bises = 1
			endif
			k=1
			f=1
			temp_month = month -1
! scrivo un file con le durate in giorni di ciascun mese ed il numero del mese
			open(112,file="data/output/mesi.dat",status='replace', access='sequential')
			do h=1, int((files_x_year*int(time/(365*(24/step_h))))+2)
				read(111,*) speedfiles(k),speedfiles(k+1),g_month(k)
				read(121,*) tempfiles(k)
				read(131,*) saltfiles(k)
				read(141,*) temp_year, temp2, temp
				g_month(k+1)=g_month(k)
				if (temp2/=temp_month) then
					if (mod(temp_year,4) == 0) then
						temp_bises = 2
					else
						temp_bises = 1
					endif
					write(112,*) days_x_month(f,temp_bises) , f
					temp_month=temp2
					f=f+1
				endif
				k=k+2
				
				if (f>12) then
					f=1
				endif
			enddo
			close(111)
			close(112)
			close(121)
			close(131)
			close(141)
		endif

!	modifico la data aumentando il contatore mensile e, nel caso, annuale
		!day = 1
		!month = month +1
		!if (month==13) then
		!	month=1
		!	year=year+1
		!endif

!	definisco l'intervallo per il prossimo aggiornamento
		delta_t=int((step_x_day)*g_month(update-2))

		!write(666,*) "delta_t = ",delta_t
		!write(666,*) "speedfiles(update) = ",speedfiles(update)
		
		temp = speedfiles(update)
		filename = temp(1:5)
		!command = "sshpass -p bonfire ssh -o StrictHostKeyChecking=no "//data_ip_code// &
		!	" ""sh -c 'cd /media/OceanographicData/OceanData/; ./transferFiles_single.sh " &
		!	//filename//" "//local_ip_code//"'"" "
		!flag = -10
		!f = 0

		!do while((flag/=0).and.(f<100))
		!	call system(command,flag)
		!	print*, flag
		!	f = f+1
		!enddo

		!if(f==100) then
		!	print*, "SSH connection error!!"
		!endif

		open(111,file="/media/OceanData/"//speedfiles(update),access='sequential')
		open(121,file="/media/OceanData/"//tempfiles(update),access='sequential')
		open(131,file="/media/OceanData/"//saltfiles(update),access='sequential')
		update=update+1
		!write(666,*) "speedfiles(update) = ",speedfiles(update)
		open(112,file="/media/OceanData/"//speedfiles(update),access='sequential')
		update=update+1

		if (first) then
			
			temp = speedfiles(update-4)
			filename2 = temp(1:5)

			!command = "sshpass -p bonfire ssh -o StrictHostKeyChecking=no "//data_ip_code// &
             !           	" ""sh -c 'cd /media/OceanographicData/OceanData/; ./transferFiles_single.sh " &
              !          	//filename2//" "//local_ip_code//"'"" "
	           !     flag = -20
               	!	f = 0
!
!	                do while((flag/=0).and.(f<100))
 !                       	call system(command,flag)
  !                      	print*, flag
   !                    	 	f = f+1
    !            	enddo
!
 !               	if(f==100) then
  !                      	print*, "SSH connection error!!"
   !             	endif

			!write(666,*) "speedfiles(update) = ",speedfiles(update-4)
			!write(666,*) "speedfiles(update) = ",speedfiles(update-3)
			open(113,file="/media/OceanData/"//speedfiles(update-4),&
				access='sequential')
			open(123,file="/media/OceanData/"//tempfiles(update-4),&
				access='sequential')
			open(133,file="/media/OceanData/"//saltfiles(update-4),&
				access='sequential')
			open(114,file="/media/OceanData/"//speedfiles(update-3),&
				access='sequential')

			j=Nnode_z
			do f=1, Nnode_z
				do k=1, Nnode_y
					do h=1, Nnode_x			
						read(113,*) ocean_speed(h,k,j,1)
						read(123,*) ocean_temp(h,k,j,1)
						read(133,*) ocean_salt(h,k,j,1)
						read(114,*) ocean_speed(h,k,j,2)
						
						read(111,*) ocean_speed(h,k,j,3)
						read(121,*) ocean_temp(h,k,j,2)
						read(131,*) ocean_salt(h,k,j,2)
						read(112,*) ocean_speed(h,k,j,4)							
					end do
				end do
				j=j-1
			end do
			close(111)
			close(121)			
			close(131)
			close(112)
			close(113)
			close(123)
			close(133)
			close(114)
			!flag = -30
			!call system("rm data/ocean/"//filename//"*.*",flag)
			!print*, flag
			!flag = -40
			!call system("rm data/ocean/"//filename2//"*.*",flag)
			!print*, flag
		else
			j=Nnode_z
			do f=1, Nnode_z
				do k=1, Nnode_y
					do h=1, Nnode_x	
						ocean_speed(h,k,f,1)=ocean_speed(h,k,f,3)
						ocean_speed(h,k,f,2)=ocean_speed(h,k,f,4)
						ocean_temp(h,k,f,1)=ocean_temp(h,k,f,2)
						ocean_salt(h,k,f,1)=ocean_salt(h,k,f,2)
						read(111,*) ocean_speed(h,k,j,3)
						read(121,*) ocean_temp(h,k,j,2)
						read(131,*) ocean_salt(h,k,j,2)
						read(112,*) ocean_speed(h,k,j,4)
					end do
				end do
				j=j-1
			end do
			close(111)
			close(121)
			close(131)
			close(112)
			!flag = -50
			!call system("rm data/ocean/"//filename//"*.*",flag)
			!print*, flag
		endif
	end subroutine modOcean

!Interpola linermente tra due medie mensile
!tra il valore del mese attuale e del mese successivo
	subroutine interPole_time(pas)
		integer(ip),intent(in) :: pas
		real(rp) p
		integer(ip) h,k,f
		
		p=pas/delta_t
		do h=1, Nnode_x
			do k=1, Nnode_y
				do f=1, Nnode_z
					ocean_speed(h,k,f,5)=(1-p)*ocean_speed(h,k,f,1)+p*&
						ocean_speed(h,k,f,3)
					ocean_speed(h,k,f,6)=(1-p)*ocean_speed(h,k,f,2)+p*&
						ocean_speed(h,k,f,4)
					ocean_temp(h,k,f,3)=(1-p)*ocean_temp(h,k,f,1)+p*&
						ocean_temp(h,k,f,2)
					ocean_salt(h,k,f,3)=(1-p)*ocean_salt(h,k,f,1)+p*&
						ocean_salt(h,k,f,2)
				end do
			end do
		end do
	end subroutine interPole_time

!Genera un nuovo rilascio
	subroutine newCohort(step,counter_larvae)
		integer(ip),intent(in) :: step
		integer(ip),dimension(2),intent(inout) :: counter_larvae
		integer(ip) a, temp_year
		
		temp_year = year - 1900

!	se ci sono rilasci attivi in oceano verifico da quanto tempo sono partiti
!	e nel caso non li seguo pi√π.
		if (counter_larvae(1)/=0) then
			a=counter_larvae(2)-counter_larvae(1)+1
			do while((eels_data(a,1)>=time_killer).and.(a<=counter_larvae(2)))
				counter_larvae(1)=counter_larvae(1)-1
				a = a + 1 
			enddo
		endif
		
		!print*, 'NC 1', year, temp_year, '>=', year_start, '<',year_stop
		!print*, month, '>=', month_start, '<', month_stop
		!print*, day

!	se sono in un anno di riproduzione ed in un mese di riproduzione
!	assegno i valori iniziali alle larve dal file eels.dat
		if ((temp_year>=year_start).and.(month>=month_start).and.&
			(month<month_stop).and.(temp_year<year_stop)) then
			!print*, 'NC ok general period -----------------------------------------------'
			!print*, 'delta_birth = ', delta_birth
			!print*, 'births = ', births
			if ((day==1).and.(month==month_start).and.(temp_year==year_start)) then
				!print*, 'NC ok lancio birth day=1 month_start year_start'
				call birth(step,counter_larvae)

			elseif ((day==1).and.(month==month_start)&
				.and.((step-eels_data(counter_larvae(2),4))>=delta_birth)&
				.and.(delta_birth/=-1)) then
				!print*, 'qui non dovrebbe entrare'
				call birth(step,counter_larvae)

			elseif ((births>1).and.(counter_larvae(2)==0).and.&
				(mod((day-1)*step_x_day,delta_birth)==0).and.&
				(delta_birth/=-1)) then
				!print*,'NC 3'
				call birth(step,counter_larvae)
			
			elseif ((counter_larvae(2)/=0).and.(delta_birth/=-1)) then
				if ((mod((step-eels_data(counter_larvae(2),4)),&
				delta_birth)==0).and.(delta_birth/=-1)) then
					!print*, 'NC 4'
					call birth(step,counter_larvae)

				endif

			elseif((day==1).and.(month==month_start).and.(delta_birth==-1)&
				.and.((step-eels_data(counter_larvae(2),4))>1)) then
				!print*, 'NC 5'
				call birth(step,counter_larvae)

			endif
		endif
		!print*,a,b
	end subroutine newCohort
	
	subroutine birth(step,counter_larvae)
	
		integer(ip),intent(in) :: step
		real(rp) temp
		integer(ip),dimension(2),intent(inout) :: counter_larvae
		integer(ip) a,b,h,i
		
		!counter_larvae(1)=counter_larvae(1)+1
		!counter_larvae(2)=counter_larvae(2)+1
		!open(3,file="data/eels.dat", access='sequential')
		if (births_x_day(births)>0) then
			a=counter_larvae(2)+1
			b=counter_larvae(2)+births_x_day(births)
			!print*, a,b, 'releases'
	!	Aumento il contatore delle nascite
			births=births+1
			do i=a, b
				!print*, i
				counter_larvae(1)=counter_larvae(1)+1
				counter_larvae(2)=counter_larvae(2)+1
				
				call gauss(eels_pos(i,1),mu_lambda,sigma_lambda)
				call gauss(eels_pos(i,2),mu_phi,sigma_phi)
					
				call gauss_limited_plus(eels_pos(i,3),mu_birth,sigma_birth, &
					up_lim_birth,down_lim_birth)
				eels_data(i,1)	= 0
				call gauss_limited(temp,0._rp,sigma_gf,log(0.5_rp),log(2._rp))
				eels_data(i,5)	= exp(temp)
				eels_data(i,2)	= L_0 * eels_data(i,5)
				eels_data(i,3)	= L_0 * eels_data(i,5)
				eels_data(i,4)	= step
				!write(601,*) eels_data(i,2), eels_data(i,5), eels_data(i,4)
				write(501,*) floor(eels_pos(i,1)), floor(eels_pos(i,2))
				eels_int(i,1) = 0
				do h=1,Nnode_y-1
					if ((eels_pos(i,2)>=ocean_coord(1,h,1,2))&
						.and.(.not.eels_pos(i,2)>=ocean_coord(1,h+1,1,2))) then
						eels_int(i,2)=h
					elseif (eels_pos(i,2)==ocean_coord(1,h+1,1,2)) then
						eels_int(i,2)=h+1
					endif
				enddo
				eels_tracking(i,1,1) = eels_pos(i,1)
				eels_tracking(i,1,2) = eels_pos(i,2)
				eels_data(i,6)=0
				eels_data(i,7)=0
				eels_int(i,3)=0
				eels_int(i,4)=0
			end do
		else
			births = births +1
		endif
		close(3)
	end subroutine birth

!Cerca il nodo pi√π vicino a sud-est e sopra (profondit√† minore)
!e le distanze dallo stesso (dx, dy, dz)
	subroutine findNode(j,nod,dx,dy,dz)
		real(rp), intent(out) :: dx,dy,dz
		integer(ip),dimension(3),intent(out) :: nod
		integer(ip),dimension(3) :: nod1
		integer(ip),intent(in) :: j
		integer(ip) i
		
		nod(1)=int((eels_pos(j,1)-ocean_coord(1,1,1,1))/delta_x)+1
		
		do i=eels_int(j,2)-2,eels_int(j,2)+2
			if ((eels_pos(j,2)>=ocean_coord(1,i,1,2))&
				.and.(.not.eels_pos(j,2)>=ocean_coord(1,i+1,1,2))) then
				nod(2)=i
			elseif (eels_pos(j,2)==ocean_coord(1,i+1,1,2)) then
				nod(2)=i+1
			endif
		enddo

		eels_int(j,2)=nod(2)

		do i=1,Nnode_z-1
			if ((eels_pos(j,3)>=ocean_coord(1,1,i,3))&
				.and.(.not.eels_pos(j,3)>=ocean_coord(1,1,i+1,3))) then
				nod(3)=i
			elseif (eels_pos(j,3)==ocean_coord(1,1,i+1,3)) then
				nod(3)=i+1
			endif
		enddo
		
		nod1(1)=nod(1)
		nod1(2)=nod(2)+1
		nod1(3)=nod(3)+1

!	cerco i delta tra i nodi y e z
		call deltaY(nod,nod1,delta_y)
		call deltaZ(nod,nod1,delta_z)
		dx=mod(eels_pos(j,1),delta_x)
		dy=mod(eels_pos(j,2),delta_y)
		dz=mod(eels_pos(j,3),delta_z)
	end subroutine


!Calcolo le velocit√† dell'oceano nel punto in cui si trova la larva
!interpolazione trilineare (x,y,z)
	subroutine interPole_space(nod,dx,dy,dz,vel_x,vel_y,temperature,salinity)
		integer(ip),dimension(3), intent(in) :: nod
		real(rp),intent(in) :: dx,dy,dz
		real(rp), intent(out) :: vel_x,vel_y,temperature,salinity
		real(rp) :: p,q,r
		p=dx/delta_x
		q=dy/delta_y
		r=dz/delta_z

		vel_x=r*(p*(q*ocean_speed(nod(1)+1,nod(2)+1,nod(3)+1,5)+(1-q)*&
			ocean_speed(nod(1)+1,nod(2),nod(3)+1,5))+(1-p)*&
			(q*ocean_speed(nod(1),nod(2)+1,nod(3)+1,5)+(1-q)*&
			ocean_speed(nod(1),nod(2),nod(3)+1,5)))+(1-r)*(p*(q*&
			ocean_speed(nod(1)+1,nod(2)+1,nod(3),5)+(1-q)*&
			ocean_speed(nod(1)+1,nod(2),nod(3),5))+(1-p)*&
			(q*ocean_speed(nod(1),nod(2)+1,nod(3),5)+(1-q)*&
			ocean_speed(nod(1),nod(2),nod(3),5)))

		vel_y=r*(p*(q*ocean_speed(nod(1)+1,nod(2)+1,nod(3)+1,6)+(1-q)*&
			ocean_speed(nod(1)+1,nod(2),nod(3)+1,6))+(1-p)*(q*&
			ocean_speed(nod(1),nod(2)+1,nod(3)+1,6)+(1-q)*&
			ocean_speed(nod(1),nod(2),nod(3)+1,6)))+(1-r)*(p*(q*&
			ocean_speed(nod(1)+1,nod(2)+1,nod(3),6)+(1-q)*&
			ocean_speed(nod(1)+1,nod(2),nod(3),6))+(1-p)*(q*&
			ocean_speed(nod(1),nod(2)+1,nod(3),6)+(1-q)*&
			ocean_speed(nod(1),nod(2),nod(3),6)))
			
		temperature=r*(p*(q*ocean_temp(nod(1)+1,nod(2)+1,nod(3)+1,3)+(1-q)*&
			ocean_temp(nod(1)+1,nod(2),nod(3)+1,3))+(1-p)*&
			(q*ocean_temp(nod(1),nod(2)+1,nod(3)+1,3)+(1-q)*&
			ocean_temp(nod(1),nod(2),nod(3)+1,3)))+(1-r)*(p*(q*&
			ocean_temp(nod(1)+1,nod(2)+1,nod(3),3)+(1-q)*&
			ocean_temp(nod(1)+1,nod(2),nod(3),3))+(1-p)*&
			(q*ocean_temp(nod(1),nod(2)+1,nod(3),3)+(1-q)*&
			ocean_temp(nod(1),nod(2),nod(3),3)))
			
		salinity=r*(p*(q*ocean_salt(nod(1)+1,nod(2)+1,nod(3)+1,3)+(1-q)*&
			ocean_salt(nod(1)+1,nod(2),nod(3)+1,3))+(1-p)*&
			(q*ocean_salt(nod(1),nod(2)+1,nod(3)+1,3)+(1-q)*&
			ocean_salt(nod(1),nod(2),nod(3)+1,3)))+(1-r)*(p*(q*&
			ocean_salt(nod(1)+1,nod(2)+1,nod(3),3)+(1-q)*&
			ocean_salt(nod(1)+1,nod(2),nod(3),3))+(1-p)*&
			(q*ocean_salt(nod(1),nod(2)+1,nod(3),3)+(1-q)*&
			ocean_salt(nod(1),nod(2),nod(3),3)))

	end subroutine interPole_space

!Aggiorna la posizione della larva in base alle velocit√† dell'oceano e alla velocit√† propria
	subroutine move(j,vel_x,vel_y) 
		integer(ip),intent(in) :: j
		real(rp), intent(in) :: vel_x,vel_y
		real(rp) :: spost_x,spost_y,spost_phi,spost_lambda,&
			vel_p, alpha, vel_tot, vel_totx, vel_toty,mu,sigma
		


!	se la larva non ha moto proprio uso solo le velocit√† dell'oceano
		if (BL_s==0) then
			spost_x=abs(vel_x*tempvel)
			spost_y=abs(vel_y*tempvel)

!	impongo la posizione lungo l'asse z in base alla lunghezza se √® lunga pi√π
!	della lunghezza limite per il moto verticale

			if (eels_data(j,2)>threshold_length) then	
				if (up_z==1) then
					mu=((dmu_night*eels_data(j,2)+mu_night_start)+&
						(dmu_day*eels_data(j,2)+mu_day_start))*0.5
					sigma=((dsigma_night*eels_data(j,2)+ &
						sigma_night_start)+(dsigma_day*eels_data(j,2)+ &
						sigma_day_start))*0.5
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,2.5_rp,mu+3*sigma)
				elseif (up_z==2) then
					mu=(dmu_day*eels_data(j,2)+mu_day_start)
					sigma=(dsigma_day*eels_data(j,2)+sigma_day_start)
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,2.5_rp,mu+3*sigma)
				elseif (up_z==5) then
					mu=((dmu_night*eels_data(j,2)+mu_night_start)+&
						(dmu_day*eels_data(j,2)+mu_day_start))*0.5
					sigma=((dsigma_night*eels_data(j,2)+ &
						sigma_night_start)+(dsigma_day*eels_data(j,2)+ &
						sigma_day_start))*0.5
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,2.5_rp,mu+3*sigma)
				elseif (up_z==6) then
					mu=(dmu_night*eels_data(j,2)+mu_night_start)
					sigma=(dsigma_night*eels_data(j,2)+sigma_night_start)
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,2.5_rp,200._rp)
				endif
			endif

!	memorizzo lo spazio percorso

!			eels_data(j,4)=eels_data(j,4)+sqrt(spost_x*spost_x+spost_y*spost_y)

!	trasformo gli spostamenti da unit√† metriche piane a unit√† angolari utilizzando per approssimare la terra l'ellissoide
!	di rotazione geocentrico WGS84
			call xy_lambdaphi(spost_x,spost_y,spost_lambda,spost_phi,&
				eels_pos(j,2),eels_pos(j,3))


!	prima di fare lo spostamento controllo la direzione della velocit√† poich√® con xy-lambdaphi ottengo angoli in valore assoluto
			eels_pos(j,1)=eels_pos(j,1)+sign(spost_lambda,vel_x)
			eels_pos(j,2)=eels_pos(j,2)+sign(spost_phi,vel_y)

		else
			
!	impongo la posizione lungo l'asse z in base alla lunghezza (z=2.9*L +60.4)
!	e calcolo la velocit√† propria della larva (v=BL_s*L)
!	se √® lunga pi√π	della lunghezza limite per il moto verticale		
			if (eels_data(j,2)>threshold_length) then
				vel_p=BL_s*eels_data(j,2)*0.001   ![m/s]
				if (up_z==1) then
					mu=((dmu_night*eels_data(j,2)+mu_night_start)+&
						(dmu_day*eels_data(j,2)+mu_day_start))*0.5
					sigma=((dsigma_night*eels_data(j,2)+ &
						sigma_night_start)+(dsigma_day*eels_data(j,2)+ &
						sigma_day_start))*0.5
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,mu-3*sigma,mu+3*sigma)
				elseif (up_z==2) then
					mu=(dmu_day*eels_data(j,2)+mu_day_start)
					sigma=(dsigma_day*eels_data(j,2)+sigma_day_start)
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,mu-3*sigma,mu+3*sigma)
				elseif (up_z==5) then
					mu=((dmu_night*eels_data(j,2)+mu_night_start)+&
						(dmu_day*eels_data(j,2)+mu_day_start))*0.5
					sigma=((dsigma_night*eels_data(j,2)+ &
						sigma_night_start)+(dsigma_day*eels_data(j,2)+ &
						sigma_day_start))*0.5
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,mu-3*sigma,mu+3*sigma)
				elseif (up_z==6) then
					mu=(dmu_night*eels_data(j,2)+mu_night_start)
					sigma=(dsigma_night*eels_data(j,2)+sigma_night_start)
					call gauss_limited_plus(eels_pos(j,3),mu,sigma,mu-3*sigma,mu+3*sigma)
				endif
			else
				vel_p=0
			endif

!	la velocit√† propria vel_p √® nella stessa direzione e verso della velocit√† dell'oceano
!	la scompongo nelle sue componenti passando dal modulo della velocita totale (oceano + vel_p)
!	e proiettando le componenti in x e y
!			
			if (alpha_nav==-1) then
				if (vel_x/=0) then
					alpha=atan(vel_y/vel_x)
					vel_tot=sqrt((vel_x*vel_x)+(vel_y*vel_y))+vel_p
		
					vel_totx=vel_tot*cos(alpha)
					vel_toty=vel_tot*sin(alpha)

					vel_totx=vel_x*abs(vel_totx)/abs(vel_x)
					vel_toty=vel_y*abs(vel_toty)/abs(vel_y)
				else
					if (vel_y/=0) then
						vel_totx=0
						vel_toty=vel_y+vel_p
					else
						eels_int(j,1)=-1
					endif
				endif

			elseif (alpha_nav==-2) then

				call random_number(alpha)
				alpha= alpha*piquarti

				vel_totx=vel_x+vel_p*cos(alpha)
				vel_toty=vel_y+vel_p*sin(alpha)

			elseif (alpha_nav==-3) then
				call gauss_limited(alpha,0.5_rp,0.25_rp,0._rp,1._rp)
				alpha= alpha*piquarti

				vel_totx=vel_x+vel_p*cos(alpha)
				vel_toty=vel_y+vel_p*sin(alpha)

			else
				vel_totx=vel_x+vel_p*cos(alpha_nav)
				vel_toty=vel_y+vel_p*sin(alpha_nav)
			endif
		
			spost_x=abs(vel_totx*tempvel)
			spost_y=abs(vel_toty*tempvel)

!	memorizzo lo spazio percorso

!			eels_data(j,4)=eels_data(j,4)+sqrt(spost_x*spost_x+spost_y*spost_y)

!	trasformo gli spostamenti da unit√† metriche piane a unit√† angolari utilizzando per approssimare la terra l'ellissoide
!	di rotazione geocentrico WGS84
			call xy_lambdaphi(spost_x,spost_y,spost_lambda,spost_phi,&
				eels_pos(j,2),eels_pos(j,3))


!	prima di fare lo spostamento controllo la direzione della velocit√† poich√® con xy-lambdaphi ottengo angoli in valore assoluto
			eels_pos(j,1)=eels_pos(j,1)+sign(spost_lambda,vel_totx)
			eels_pos(j,2)=eels_pos(j,2)+sign(spost_phi,vel_toty)
		endif 
		
	end subroutine move


!Converte le distanze metriche in distanze angolari phi lambda
	subroutine xy_lambdaphi(spost_x,spost_y,spost_lambda,spost_phi,&
		phi,depth)

		real(rp),intent(in) :: spost_x,spost_y,phi,depth
		real(rp) phirad
		real(rp),intent(out) :: spost_phi,spost_lambda
		real(rp) rm				!Raggio del meridiano
		real(rp) rpa			!Raggio del parallelo

!	trasformo phi in radianti
		phirad=(phi*pi180)

!	calcolo il raggio del parallelo (rpa=(R-z)*cos(phi))
		rpa=(R-depth)*cos(phirad)
		rm=R-depth
		spost_lambda=(spost_x/rpa)*invpi180
		spost_phi=(spost_y/rm)*invpi180
	end subroutine xy_lambdaphi

!Determina il l'ampiezza delta_y in latitudine tra i due nodi
	subroutine deltaY(nod,nod1,delta_y)

		integer(ip),dimension(3), intent(in) :: nod,nod1
		real(rp), intent(out) :: delta_y

	
		delta_y = abs(ocean_coord(nod(1),nod(2),nod(3),2)-&
			ocean_coord(nod1(1),nod1(2),nod1(3),2))

	end subroutine deltaY

!Determina l'ampiezza delta_z lungo l'asse z tra i due nodi
	subroutine deltaZ(nod,nod1,delta_z)

		integer(ip),dimension(3), intent(in) :: nod,nod1
		real(rp), intent(out) :: delta_z

		delta_z = abs(ocean_coord(nod(1),nod(2),nod(3),3)-&
			ocean_coord(nod1(1),nod1(2),nod1(3),3))
		
	end subroutine deltaZ

	subroutine grow(j,temperature)
		integer(ip),intent(in) :: j
		real(rp),intent(in) :: temperature
		
		eels_data(j,3)	= eels_data(j,2)
		eels_data(j,1)	= eels_data(j,1) + 1
		eels_data(j,2)	= (alpha_growth * (eels_data(j,1) * day_x_step * month_x_day) ** beta_growth + L_0)*eels_data(j,5) !(L_inf*eels_data(j,5) - eels_data(j,3))*(1-exp(-brody)) + eels_data(j,3) !eels_data(j,2) + lambda(int(eels_data(j,1))) * eels_data(j,5)
		
		if (temperature>= 20) then
			eels_data(j,7)	= eels_data(j,7) + 1*day_x_step
		elseif (temperature>10) then
			eels_data(j,7)	= eels_data(j,7) + (temperature*.1 - 1)*day_x_step
		endif
	end subroutine

!	subroutine ptinpoly(x,y,n,poly,inout)
!		integer(ip),intent(in)               :: n
!		integer(ip),intent(out)              :: inout
!		real(rp),intent(in)                  :: x,y
!		real(rp),dimension(1:n,2),intent(in) :: poly
!
!		real(rp) xi
!		integer(ip) count,temp,i
!
!		count = 0
!
!		do i=1,n
!			
!			temp=i+1
!			if (temp==n+1) temp=1
!
!			if (poly(i,2)/=poly(temp,2))then
!				if ((poly(i,2)-y)*(poly(temp,2)-y)<=0) then
!
!					xi = (y-poly(i,2))*(poly(temp,1)-poly(i,1))/(poly(temp,2)-poly(i,2)) + poly(i,1)
!
!					if ((xi>x).and.(y/=poly(i,2))) then
!						
!						if (y==poly(temp,2)) then
!							
!							if ((poly(i,2)-y)*(poly(temp+1,2)-y)<0) then
!
!								count = count+1
!
!							elseif ((poly(i,2)-y)*(poly(temp+1,2)-y)==0) then
!								
!								if ((poly(i,2)-y)*(poly(temp+2,2)-y)<0) then
!
!									count = count+1
!
!								endif
!							
!							endif
!
!						else
!
!							count = count+1
!
!						endif
!					elseif (xi==x) then
!						inout = 1
!						return
!					endif
!
!				endif
!			elseif ((poly(i,1)-x)*(poly(temp,1)-x)<0) then
!				inout = 1
!				return
!			endif
!		enddo
!
!		if (mod(count,2)==0) then
!			inout = 0
!		else
!			inout = 1
!		endif
!	
!	end subroutine

end module eels

