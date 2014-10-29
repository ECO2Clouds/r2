MODULE mo_kind

  IMPLICIT NONE

  INTEGER, PARAMETER :: sp = SELECTED_REAL_KIND(6,37)  
  INTEGER, PARAMETER :: dp = SELECTED_REAL_KIND(12,307)
  INTEGER, PARAMETER :: i4 = SELECTED_INT_KIND(9)
  INTEGER, PARAMETER :: i8 = SELECTED_INT_KIND(14)

END MODULE mo_kind

MODULE mo_constants
USE mo_kind,      ONLY: wp => dp
IMPLICIT NONE


REAL (wp), PARAMETER :: pi   = 3.14159265358979323846_wp
REAL (wp), PARAMETER :: eps=1.e-8

END MODULE mo_constants



module distributions
use mo_constants, only: wp
implicit none


contains


SUBROUTINE ran0(idum,y) 
!
! ran0 portable pseudorandom number generator
! of Park and Miller, as described in Numerical Recipes;
! this version uses basic set of integer parameters
!
USE mo_constants, only: wp
IMPLICIT NONE

INTEGER, PARAMETER :: IA   = 16807
INTEGER, PARAMETER :: IM   = 2147483647
INTEGER, PARAMETER :: IQ   = 127773
INTEGER, PARAMETER :: IR   = 2836
INTEGER, PARAMETER :: MASK = 123456789

REAL(wp), PARAMETER:: AM = 1._wp/IM

INTEGER :: k

INTEGER, INTENT(INOUT) :: idum
REAL(wp),INTENT(OUT)   :: y

k=idum/IQ
idum=IA*(idum-k*IQ) -IR*k
if(idum<=0) idum=idum+IM
y=AM*idum

END SUBROUTINE ran0


SUBROUTINE ran0b(idum,y) 
!
! ran0 portable pseudorandom number generator
! of Park and Miller, as described in Numerical Recipes;
! this version uses one possible variant of integer parameters
!
USE mo_constants, only: wp
IMPLICIT NONE

INTEGER, PARAMETER :: IA   = 48271
INTEGER, PARAMETER :: IM   = 2147483647
INTEGER, PARAMETER :: IQ   = 44488
INTEGER, PARAMETER :: IR   = 3399
INTEGER, PARAMETER :: MASK = 123456789

REAL(wp), PARAMETER:: AM = 1._wp/IM

INTEGER :: k

INTEGER, INTENT(INOUT) :: idum
REAL(wp),INTENT(OUT)   :: y

k=idum/IQ
idum=IA*(idum-k*IQ) -IR*k
if(idum<=0) idum=idum+IM
y=AM*idum

END SUBROUTINE ran0b



SUBROUTINE ran0c(idum,y) 
!
! ran0 portable pseudorandom number generator
! of Park and Miller, as described in Numerical Recipes;
! this version uses another possible variant of integer
! parameters
!
USE mo_constants, only: wp
IMPLICIT NONE

INTEGER, PARAMETER :: IA   = 69621
INTEGER, PARAMETER :: IM   = 2147483647
INTEGER, PARAMETER :: IQ   = 30845
INTEGER, PARAMETER :: IR   = 23902
INTEGER, PARAMETER :: MASK = 123456789

REAL(wp), PARAMETER:: AM = 1._wp/IM

INTEGER :: k

INTEGER, INTENT(INOUT) :: idum
REAL(wp),INTENT(OUT)   :: y

k=idum/IQ
idum=IA*(idum-k*IQ) -IR*k
if(idum<=0) idum=idum+IM
y=AM*idum

END SUBROUTINE ran0c


SUBROUTINE ran1(idum,y) 
!
! ran1 portable pseudorandom number generator
! of Park and Miller with Bays-Durham shuffle,
! as described in Numerical Recipes
!
USE mo_constants, only: wp
IMPLICIT NONE

INTEGER, PARAMETER :: IA   = 16807
INTEGER, PARAMETER :: IM   = 2147483647
INTEGER, PARAMETER :: IQ   = 127773
INTEGER, PARAMETER :: IR   = 2836
INTEGER, PARAMETER :: NTAB = 32
INTEGER, PARAMETER :: NDIV = (1+(IM-1)/NTAB)


INTEGER, PARAMETER :: MASK = 123456789

REAL(wp), PARAMETER:: AM = 1._wp/IM
REAL(wp), PARAMETER:: NEPS = 1.2e-7
REAL(wp), PARAMETER:: RNMX = (1._wp-NEPS)

INTEGER :: j,k
INTEGER :: iy=0
INTEGER :: iv(0:NTAB)

REAL(wp) :: temp

INTEGER, INTENT(INOUT) :: idum
REAL(wp),INTENT(OUT)   :: y


DO  j=NTAB+7,0,-1


    k=idum/IQ
    idum=IA*(idum-k*IQ) -IR*k
    if(idum<=0) idum=idum+IM
    if(j<NTAB) iv(j) = idum

ENDDO

iy=iv(0)

k=idum/IQ
idum=IA*(idum-k*IQ) -IR*k
if(idum<=0) idum=idum+IM

j=iy/NDIV
iv(j)=idum
temp=AM*iy

IF (temp > RNMX) THEN

y=RNMX
  
ELSE

y=temp

ENDIF





k=idum/IQ
idum=IA*(idum-k*IQ) -IR*k
if(idum<=0) idum=idum+IM
y=AM*idum

END SUBROUTINE ran1



SUBROUTINE ran2(idum,y) 
!
! ran2 portable pseudorandom number generator
! of Park and Miller with Bays-Durham shuffle
! and L'Ecuyer coupling of two ran1 generators with
! different periods,
! as described in Numerical Recipes
!
USE mo_constants, only: wp
IMPLICIT NONE

INTEGER, PARAMETER :: IA1   = 40014
INTEGER, PARAMETER :: IM1   = 2147483563
INTEGER, PARAMETER :: IQ1   = 53668
INTEGER, PARAMETER :: IR1   = 12211


INTEGER, PARAMETER :: IA2   = 40692
INTEGER, PARAMETER :: IM2   = 2147483399
INTEGER, PARAMETER :: IQ2   = 52774
INTEGER, PARAMETER :: IR2   = 3791

INTEGER, PARAMETER :: INM1 = IM1-1

INTEGER, PARAMETER :: NTAB = 32
INTEGER, PARAMETER :: NDIV = (1+(INM1)/NTAB)


INTEGER, PARAMETER :: MASK = 123456789

REAL(wp), PARAMETER:: AM = 1._wp/IM1
REAL(wp), PARAMETER:: NEPS = 1.2e-7
REAL(wp), PARAMETER:: RNMX = (1._wp-NEPS)

INTEGER :: j,k
INTEGER :: iy=0
INTEGER :: idum2=MASK
INTEGER :: iv(0:NTAB)

REAL(wp) :: temp

INTEGER, INTENT(INOUT) :: idum
REAL(wp),INTENT(OUT)   :: y


DO  j=NTAB+7,0,-1


    k=idum/IQ1
    idum=IA1*(idum-k*IQ1) -IR1*k
    if(idum<=0) idum=idum+IM1
    if(j<NTAB) iv(j) = idum

ENDDO

iy=iv(0)

k=idum/IQ1
idum=IA1*(idum-k*IQ1) -IR1*k
if(idum<=0) idum=idum+IM1

k=idum2/IQ2
idum2=IA2*(idum2-k*IQ2) -IR2*k
if(idum2<=0) idum2=idum2+IM2



j=iy/NDIV
iy=iv(j)-idum2
iv(j)=idum



IF (iy<1) iy=iy+INM1

temp=AM*iy

IF (temp > RNMX) THEN

y=RNMX
  
ELSE

y=temp

ENDIF



END SUBROUTINE ran2

subroutine gauss_limited_plus(x,mu,sigma,up_lim,down_lim)
	implicit none
	real(kind=wp), intent(out) :: x
	real(kind=wp), intent(in) :: mu,sigma,up_lim,down_lim
	real(kind=wp)::  y,v1,v2,fac,sqr

	do
   		call random_number(x)
   		call random_number(y)
   		v1=2._wp*x-1._wp
   		v2=2._wp*y-1._wp
   		sqr=v1*v1+v2*v2
   		if((sqr.le.1._wp).and.(sqr.gt.0._wp)) then
     			fac= sqrt(-2._wp*log(sqr)/sqr)
     			x=sigma*v1*fac+mu
			if ((x>=up_lim).and.(x<=down_lim).and.(x>=2.5_wp)) then
     				exit
			endif
   		endif
	enddo
end subroutine gauss_limited_plus

subroutine gauss_limited(x,mu,sigma,up_lim,down_lim)
	implicit none
	real(kind=wp), intent(out) :: x
	real(kind=wp), intent(in) :: mu,sigma,up_lim,down_lim
	real(kind=wp)::  y,v1,v2,fac,sqr

	do
   		call random_number(x)
   		call random_number(y)
   		v1=2._wp*x-1._wp
   		v2=2._wp*y-1._wp
   		sqr=v1*v1+v2*v2
   		if((sqr.le.1._wp).and.(sqr.gt.0._wp)) then
     			fac= sqrt(-2._wp*log(sqr)/sqr)
     			x=sigma*v1*fac+mu
			if ((x>=up_lim).and.(x<=down_lim)) then
     				exit
			endif
   		endif
	enddo
end subroutine gauss_limited

subroutine gauss(x,mu,sigma)
	implicit none
	real(kind=wp), intent(out) :: x
	real(kind=wp), intent(in) :: mu,sigma
	real(kind=wp)::  y,v1,v2,fac,sqr

	do
   		call random_number(x)
   		call random_number(y)
   		v1=2._wp*x-1._wp
   		v2=2._wp*y-1._wp
   		sqr=v1*v1+v2*v2
   		if((sqr.le.1._wp).and.(sqr.gt.0._wp)) then
     			fac= sqrt(-2._wp*log(sqr)/sqr)
     			x=sigma*v1*fac+mu
     			exit
   		endif
	enddo
end subroutine gauss

end module distributions















