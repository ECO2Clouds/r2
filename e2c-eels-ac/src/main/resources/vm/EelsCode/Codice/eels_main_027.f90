!				-----------------------------
!				    Marcello Schiavina
!				Master thesis October 2008 ;)
!				-----------------------------

program leptocephali

	USE eels
	use distributions

	implicit none

	!passo di simulazione e passo di aggiornamento dei dati oceanici
	integer(ip) step,stepup 
	integer(ip),dimension(2) :: counter_larvae 
!	1 - moving larvae 2 - started larvae
	

!	inizializzo il modello: oceano, anguille e parametri della simulazione
	call initialize(step,stepup,counter_larvae(:))

!	simulazione
	call sim(step,stepup,counter_larvae(:))

end program leptocephali


subroutine sim(step,stepup,counter_larvae)
	
	USE eels
	implicit none
	
	real(rp),parameter :: inv3=1._rp/3._rp
	real(rp),parameter :: inv30=1._rp/30._rp
	real(rp),parameter :: m=-9._rp/11._rp
	real(rp),parameter :: q=-359._rp/11._rp

	integer(ip),intent(inout) :: step,stepup
	integer(ip),dimension(2),intent(inout) :: counter_larvae 
	integer(ip),dimension(3) :: nod	!Vettore con gli id di un nodo
	integer(ip) j,i,h,k,a,b,c,f , BS_x, BS_y, count_success, t1, t2, t_rate, t_max
	real(rp) real_temp,dx,dy,dz,vel_x,vel_y, survival, temperature, salinity,sigma
	!real(rp_low),dimension(11,8,2) :: BastStrehlow
	!real(rp_low),dimension(20,30,2) :: BastStrehlow2	

! 	inizio la simulazione
	
	
	!BastStrehlow(:,:,1)=0._rp_low
	!BastStrehlow(:,:,2)=0._rp_low
	
	!BastStrehlow2(:,:,1)=0._rp_low
	!BastStrehlow2(:,:,2)=0._rp_low

!	Inizializzo il counter dei successi
	count_success = 0;

!	apro il file in cui scrivo gli arrivi e scrivo l'intestazione
	call initFiles

!	apro il file dove scrivo le taglie alla nascita
	!open(601,file="data/eelsdata.dat",status='replace',&
	!access='sequential')
	open(501,file="data/output/eelscoord.dat",status='replace',&
	access='sequential')


	do while(step<=time)	!ciclo su tutto il tempo
		
	call system_clock(t1,t_rate,t_max)

!	se step = k*delta_t (k diverso da 0 appartenente ad N)
!	allora aggiorno il campo delle velocita (modOcean)
		if ((mod((step-stepup),int(delta_t))==0).and.(step/=1)) then
			stepup=step
			call modOcean(.false.)
		endif

! 	controllo se (in base ai parametri impostati all'inizio)
!	devo smettere di seguire delle anguille perchè partite
!	da ormai troppo tempo (time_killer) o se devo farne partire
!	altre (delta_birth)
		call newCohort(step,counter_larvae)


!	se ci sono anguille da muovere allora procedo con la simulazione in questo passo
		if (counter_larvae(1)/=0) then

!	interpolo le velocità tra le due medie mensili
			call interPole_time(step-stepup)

!	passo i parametri per ciclare sulle anguille in singole variabili:
!	controllo quante anguille sono partite dall'inizio e sottreggo il numero
!	di anguille in movimento per sapere in che punto
! 	della matrice delle anguille (eels_pos) partire

			a=counter_larvae(2)-counter_larvae(1)+1

!	totale delle anguille partite
			b=counter_larvae(2)
			i=0

! 	ciclo su tutte le anguille da muovere333
			do j=a,b

!	controllo che l'anguilla da spostare non sia già uscita dal campo
				if (eels_int(j,1)>=0) then
! 	controllo se l'anguilla sia uscita dal campo nell'ultimo passo
					if (((eels_pos(j,1)<((-length_x)+&
						ocean_coord(Nnode_x,1,1,1)))&
						.or.(eels_pos(j,1)>ocean_coord(Nnode_x,1,1,1)))&
						.or.((eels_pos(j,2)<0).or.(eels_pos(j,2)>=length_y))&
						.or.(eels_pos(j,3)>=length_z)) then

!	pongo l'info "stop" a 1 		
						if (eels_int(j,1)/=4) then
							eels_int(j,1)=-1
						else
							eels_int(j,1)=-4
						endif
					else
					
! 	trovo il nodo di riferimento del settore in cui c'è l'anguilla ed interpolo le velocità nello spazio
						call findNode(j,nod,dx,dy,dz)
						call interPole_space(nod,dx,dy,dz,vel_x,vel_y,temperature,salinity)


!	controllo se sono in prossimita del transetto "Florida" e nel caso lo scrivo sul file
						if ((year==84).and.(((month==7).and.(day>23)).or.&
						((month==8).and.(day<14)))) then
							if ((eels_pos(j,1)<-68).and.&
							(eels_pos(j,2)<(m*eels_pos(j,1)+q))) then
								
!	segnalo il possibile passaggio del transetto
								eels_int(j,1)=1
							endif
						endif
						
!	controllo se se sono in prossimita del transetto "Florida" e nel caso lo scrivo sul file
						if ((year==84).and.(((month==9).and.(day>28)).or.&
						((month==10).and.(day<20)))) then
							if ((eels_pos(j,1)<-68).and.&
							(eels_pos(j,2)<(m*eels_pos(j,1)+q))) then
								
!	segnalo il possibile passaggio del transetto
								eels_int(j,1)=2
							endif
						endif
						
						eels_data(j,6)=eels_data(j,6)+temperature
						eels_int(j,3)=eels_int(j,3)+1
						!eels_data(j,7)=eels_data(j,7)+salinity
						!eels_int(j,4)=eels_int(j,4)+1
						
! 	muovo l'anguilla
						call move(j,vel_x,vel_y)

!	faccio crescere l'anguilla
						call grow(j,temperature)
						
!	valuto la sopravvivenza	
						gamma_mortality=a_mortality*exp(-Act_energy/(Boltzmann*(temperature+273.15)))
						
						call random_number(survival)
						sigma=exp(-gamma_mortality*(alpha_weight*eels_data(j,3)**beta_weight)**b_mortality)
							
						if (((survival>sigma).or.(eels_data(j,1)>=time_killer)).or.(eels_int(j,1)<0)) then

							if (eels_int(j,1)/=4) then
								eels_int(j,1)=-1
							else
								eels_int(j,1)=-4
							endif
						else	
						

!	aggiorno la mesh delle isolunghezze
							if (isolength(nod(1),nod(2))>eels_data(j,2)) then
								isolength(nod(1),nod(2))=eels_data(j,2)
							endif

							!meanlength(nod(1),nod(2),int((step-1)*day_x_step*month_x_day)+1,1) = meanlength(nod(1),&
							!	nod(2),int((step-1)*day_x_step*month_x_day)+1,1) + eels_data(j,2)
							!meanlength(nod(1),nod(2),int((step-1)*day_x_step*month_x_day)+1,2) = meanlength(nod(1),&
							!	nod(2),int((step-1)*day_x_step*month_x_day)+1,2) +1
					
	!	aggiorno la mesh di confronto con Bast&Strehlow
							!if ((eels_pos(j,1)>-35).and.(eels_pos(j,1)<=-3)) then
							!	if ((eels_pos(j,2)>=32).and.(eels_pos(j,2)<65)) then
							!		BS_x=int(-(eels_pos(j,1)-1)*0.25_rp)
							!		BS_y=int((eels_pos(j,2)-29)*inv3)
							!		BastStrehlow(BS_y,BS_x,1)=BastStrehlow(BS_y,BS_x,1)&
							!		+eels_data(j,2)
							!		BastStrehlow(BS_y,BS_x,2)=BastStrehlow(BS_y,BS_x,2)+1
							!	endif
							!endif
					
	!	aggiorno la mesh di confronto con Bast&Strehlow2
							!if ((year==84).and.(month>=7).and.(month<=9)) then
							!	if ((eels_pos(j,1)>-35).and.(eels_pos(j,1)<=-5)) then
							!		if ((eels_pos(j,2)>=35).and.(eels_pos(j,2)<55)) then
							!			BS_x=int(-(eels_pos(j,1)+4))
							!			BS_y=int((eels_pos(j,2)-34))
							!			BastStrehlow2(BS_y,BS_x,1)=BastStrehlow2(BS_y,BS_x,1)&
							!			+eels_data(j,2)
							!			BastStrehlow2(BS_y,BS_x,2)=BastStrehlow2(BS_y,BS_x,2)+1
							!		endif
							!	endif
							!endif

	!	controllo se ho attraversato il transetto dei 20°W e nel caso lo scrivo sul file
							if ((eels_pos(j,1)>preboundary).and.(eels_int(j,1)<3)) then
								call scriviSuFile(11,j,step,vel_x)
	!	segnalo il passagio del transetto
								eels_int(j,1)=3
							endif
				

	!	controllo se ho attraversato il transetto finale e nel caso lo scrivo sul file
							if ((eels_pos(j,1)>boundary).and.(eels_int(j,1)<4)) then
								count_success = count_success + 1
								call scriviSuFile(9,j,step,eels_data(j,7))
	!	segnalo il passagio del transetto
								eels_int(j,1)=4
								eels_data(j,6)=eels_data(j,6)/eels_int(j,3)
								!eels_data(j,7)=eels_data(j,7)/eels_int(j,4)
								call track(j)
								eels_data(j,6) = 0
								!eels_data(j,7) = 0
								eels_int(j,3) = 0
								!eels_int(j,4) = 0
							endif
					
	!	Se la larva era in prossimità del transetto "Florida" controllo se lo ha attraversato
							if ((eels_int(j,1)==1).and.((eels_pos(j,2)>23).and.&
							(eels_pos(j,2)>(m*eels_pos(j,1)+q)))) then
								call scriviSuFile(7,j,step,temperature)
								eels_int(j,1)=0
							elseif (eels_int(j,1)==1) then
								eels_int(j,1)=0
							endif
					
	!	Se la larva era in prossimità del transetto "Florida" controllo se lo ha attraversato					
							if ((eels_int(j,1)==2).and.((eels_pos(j,2)>23).and.&
							(eels_pos(j,2)>(m*eels_pos(j,1)+q)))) then
								call scriviSuFile(8,j,step,temperature)
								eels_int(j,1)=0
							elseif (eels_int(j,1)==2) then
								eels_int(j,1)=0
							endif

	!	Se è il 15 del mese Aggiorno il file di tracking 
	!	Scrivo la posizione della larva sul file delle traiettorie								

							if ((day==15).and.(eels_int(j,1)/=4)) then
								eels_data(j,6)=eels_data(j,6)/eels_int(j,3)
								!eels_data(j,7)=eels_data(j,7)/eels_int(j,4)
								call track(j)
								eels_data(j,6) = 0
								!eels_data(j,7) = 0
								eels_int(j,3) = 0
								!eels_int(j,4) = 0
							endif
						endif
					endif
				endif
			end do
		endif
		call system_clock(t2,t_rate,t_max)
		print*,step, real ( t2 - t1 ) / real ( t_rate )
		step	= step + 1
		day	= day + day_x_step
		
! aggiorno la variabile per il posizionamento verticale


		up_z	= up_z + 1
		if (up_z==step_x_day) then
			up_z=0
		endif
		
		!se era un giorno di scrittura posizioni chiudo il file
		!if (day==15) then
		!	close(200)
		!endif

	end do
	
! END SIMULATION
! RE-ORGANIZING FILES AND DATA

	!do a=1,11
	!	do b=1,8
	!		BastStrehlow(a,b,1)=BastStrehlow(a,b,1)/BastStrehlow(a,b,2)
	!	enddo
	!enddo
	
	!open(10,file="data/output/baststrehlowL.dat",status='replace',access='sequential')
	!write(10,*) BastStrehlow(:,:,1)
	!close(10)
	
	!open(10,file="data/output/baststrehlowN.dat",status='replace',access='sequential')
	!write(10,*) BastStrehlow(:,:,2)
	!close(10)
	
	
	!do a=1,20
	!	do b=1,30
	!		BastStrehlow2(a,b,1)=BastStrehlow2(a,b,1)/BastStrehlow2(a,b,2)
	!	enddo
	!enddo
	
	!do a=1, Nnode_x
	!	do b=1,Nnode_y
	!		do c=1,months
	!			if (meanlength(a,b,c,2)/=0) then
	!				meanlength(a,b,c,1)=meanlength(a,b,c,1)/meanlength(a,b,c,2)
	!			endif
	!		enddo
	!	enddo
	!enddo
	
	!open(10,file="data/output/baststrehlow2L.dat",status='replace',access='sequential')
	!write(10,*) BastStrehlow2(:,:,1)
	!close(10)
	
	!open(10,file="data/output/baststrehlow2N.dat",status='replace',access='sequential')
	!write(10,*) BastStrehlow2(:,:,2)
	!close(10)
	
	open(10,file="data/output/isolength.dat",status='replace',access='sequential')
	write(10,*) isolength(:,:)
	close(10)
	
	call tracking_transfer(count_success)

	!open(10,file="data/output/meanlength.dat",status='replace',access='sequential')
	!write(10,*) meanlength(:,:,:,1)
	!close(10)

	!open(10,file="data/output/meanlength_abb.dat",status='replace',access='sequential')
	!write(10,*) meanlength(:,:,:,2)
	!close(10)
	
	open(10,file="data/output/tracking_lambda.dat",status='replace',access='sequential')
	write(10,*) eels_tracking_final(:,:,1)
	close(10)
	
	open(10,file="data/output/tracking_phi.dat",status='replace',access='sequential')
	write(10,*) eels_tracking_final(:,:,2)
	close(10)

	open(10,file="data/output/tracking_T.dat",status='replace',access='sequential')
	write(10,*) eels_tracking_final(:,:,3)
	close(10)
	
!	open(10,file="data/output/tracking_z.dat",status='replace',access='sequential')
!	write(10,*) eels_tracking(:,:,3)
!	close(10)
	
	close(7)
	close(8)
	close(9)
	close(11)
	!close(601)
	close(501)

end subroutine

subroutine scriviSuFile(idFile,idEel,step,moreinfo)

	USE eels
	implicit none

	INTEGER(ip),intent(in) :: idFile, idEel, step
	REAL(rp),intent(in) :: moreinfo

	write(idFile,*) eels_pos(idEel,1),eels_pos(idEel,2),eels_pos(idEel,3),&
		eels_data(idEel,1),idEel,step, moreinfo, eels_data(idEel,2)

end subroutine

!subroutine scriviSuFile2(idFile,idEel)
!
!	USE eels
!	implicit none
!
!	INTEGER(ip),intent(in) :: idFile, idEel
!	
!	write(idFile,*) eels_pos(idEel,1),eels_pos(idEel,2),eels_pos(idEel,3),&
!		eels_data(idEel,1),eels_data(idEel,2),idEel, eels_data(idEel,6), &
!		eels_data(idEel,7)
!
!end subroutine

subroutine initFiles

	USE eels
	implicit none

	open(7,file="data/output/florida1.dat",status='replace',&
	access='sequential')

	open(8,file="data/output/florida2.dat",status='replace',&
	access='sequential')

	open(9,file="data/output/finish.dat",status='replace',&
	access='sequential')
	
	open(11,file="data/output/prefinish.dat",status='replace',&
	access='sequential')
	
end subroutine

!subroutine initFiles2()
!
!	USE eels
!	implicit none
!
!	INTEGER(ip) i
!	CHARACTER(LEN=14) :: year_c
!	CHARACTER(LEN=14) :: month_c
!	CHARACTER(LEN=2) :: year_c2
!	CHARACTER(LEN=2) :: month_c2
!	
!		
!	write(month_c,*) month
!	
!	month_c2=adjustl(month_c)
!	
!	write(year_c,*) year
!	
!	year_c2=adjustl(year_c)
!	
!	open(200,file="data/output/"//trim(month_c2)//"-"//trim(year_c2)//".dat",&
!		status='REPLACE',access='sequential')
!
!end subroutine

subroutine track(j)

	USE eels
	implicit none
	
	INTEGER(ip), intent(in) :: j
	INTEGER(ip) age_m

	age_m = ceiling(eels_data(j,1)*day_x_step*month_x_day)
	if (eels_int(j,1)==4) then
		age_m = age_m + 1
	endif
	if (age_m<49) then
		eels_tracking(j,age_m+1,1) = eels_pos(j,1)
		eels_tracking(j,age_m+1,2) = eels_pos(j,2)
		eels_tracking(j,age_m+1,3) = eels_data(j,6)
	endif
	
! aGGIUNGERE LA TEMPERATURA COME TERZO DATO
! tRASFORMARE IN MATRICIONA CHE LE SEGUA PER TUTTO IL TEMPO
! POI FILTRARLA SUI SOLI ARRIVI POSITIVI AL TRANSETTO!!

	!if (eels_data(j,1)==2) then
	!	eels_tracking(j,2,1) = eels_pos(j,1)
	!	eels_tracking(j,2,2) = eels_pos(j,2)
		!eels_tracking(j,1,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==4) then
	!	eels_tracking(j,3,1) = eels_pos(j,1)
	!	eels_tracking(j,3,2) = eels_pos(j,2)
		!eels_tracking(j,2,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==8) then
	!	eels_tracking(j,4,1) = eels_pos(j,1)
	!	eels_tracking(j,4,2) = eels_pos(j,2)
	!	!eels_tracking(j,3,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==16) then
	!	eels_tracking(j,5,1) = eels_pos(j,1)
	!	eels_tracking(j,5,2) = eels_pos(j,2)
	!	!eels_tracking(j,4,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==32) then
	!	eels_tracking(j,6,1) = eels_pos(j,1)
	!	eels_tracking(j,6,2) = eels_pos(j,2)
	!	!eels_tracking(j,5,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==64) then
	!	eels_tracking(j,7,1) = eels_pos(j,1)
	!	eels_tracking(j,7,2) = eels_pos(j,2)
	!	!eels_tracking(j,6,3) = eels_pos(j,3)
	!elseif (eels_data(j,1)==128) then
	!	eels_tracking(j,8,1) = eels_pos(j,1)
	!	eels_tracking(j,8,2) = eels_pos(j,2)
		!eels_tracking(j,7,3) = eels_pos(j,3)	
	!endif
	
end subroutine track

subroutine tracking_transfer(counter)

	USE eels
	implicit none

	INTEGER(ip), intent(in) :: counter
	INTEGER(ip) :: j,i,k

	j=1
	i=0
	
	deallocate(isolength)
	deallocate(eels_data)
	deallocate(eels_pos)
	
	allocate(eels_tracking_final(counter,size(eels_tracking,2),3))

	do while (j<=Neeltot)
		if (abs(eels_int(j,1))==4) then
			i = i+1
			do k = 1, size(eels_tracking,2)
				eels_tracking_final(i,k,1) = eels_tracking(j,k,1)
				eels_tracking_final(i,k,2) = eels_tracking(j,k,2)
				eels_tracking_final(i,k,3) = eels_tracking(j,k,3)
			enddo
		endif
		j = j+1
	enddo
	
end subroutine tracking_transfer
