REM
REM Copyright (c) 1999, 2001, Oracle Corporation.  All rights reserved.  
REM
@echo off
if (%1) == () goto usage
if (%1) == (cdemo6) goto cplusplus
if (%1) == ("cdemo6") goto cplusplus
if (%1) == (CDEMO6) goto cplusplus
if (%1) == ("CDEMO6") goto cplusplus
if (%1) == (ociucb) goto callucb
if (%1) == ("ociucb") goto callucb
if (%1) == (OCIUCB) goto callucb
if (%1) == ("OCIUCB") goto callucb

if (%1) == (cdemoucb) goto callucb2
if (%1) == (CDEMOUCB) goto callucb2

if (%1) == (occiblob) goto occimake
if (%1) == ("occiblob") goto occimake
if (%1) == (OCCIBLOB) goto occimake
if (%1) == ("OCCIBLOB") goto occimake
if (%1) == (occidml) goto occimake
if (%1) == ("occidml") goto occimake
if (%1) == (OCCIDML) goto occimake
if (%1) == ("OCCIDML") goto occimake
if (%1) == (occiclob) goto occimake
if (%1) == ("occiclob") goto occimake
if (%1) == (OCCICLOB) goto occimake
if (%1) == ("OCCICLOB") goto occimake
if (%1) == (occicoll) goto occimake
if (%1) == ("occicoll") goto occimake
if (%1) == (OCCICOLL) goto occimake
if (%1) == ("OCCICOLL") goto occimake
if (%1) == (occidesc) goto occimake
if (%1) == ("occidesc") goto occimake
if (%1) == (OCCIDESC) goto occimake
if (%1) == ("OCCIDESC") goto occimake
if (%1) == (occiobj) goto occimakeobj
if (%1) == ("occiobj") goto occimakeobj
if (%1) == (OCCIOBJ) goto occimakeobj
if (%1) == ("OCCIOBJ") goto occimakeobj
if (%1) == (occipobj) goto occimakeobj
if (%1) == ("occipobj") goto occimakeobj
if (%1) == (OCCIPOBJ) goto occimakeobj
if (%1) == ("OCCIPOBJ") goto occimakeobj
if (%1) == (occiinh) goto occimakeobj
if (%1) == ("occiinh") goto occimakeobj
if (%1) == (OCCIINH) goto occimakeobj
if (%1) == ("OCCIINH") goto occimakeobj
if (%1) == (occipool) goto occimake
if (%1) == ("occipool") goto occimake
if (%1) == (OCCIPOOL) goto occimake
if (%1) == ("OCCIPOOL") goto occimake
if (%1) == (occiproc) goto occimake
if (%1) == ("occiproc") goto occimake
if (%1) == (OCCIPROC) goto occimake
if (%1) == ("OCCIPROC") goto occimake
if (%1) == (occistre) goto occimake
if (%1) == ("occistre") goto occimake
if (%1) == (OCCISTRE) goto occimake
if (%1) == ("OCCISTRE") goto occimake

if (%1) == (EXTDEMO2) goto dllmake
if (%1) == ("EXTDEMO2") goto dllmake
if (%1) == (extdemo2) goto dllmake
if (%1) == ("extdemo2") goto dllmake
if (%1) == (EXTDEMO5) goto dllmake2
if (%1) == ("EXTDEMO5") goto dllmake2
if (%1) == (extdemo5) goto dllmake2
if (%1) == ("extdemo5") goto dllmake2
if (%1) == (cdemodp) goto cdemodpmake
if (%1) == ("cdemodp") goto cdemodpmake
if (%1) == (CDEMODP) goto cdemodpmake
if (%1) == ("CDEMODP") goto cdemodpmake
if (%1) == (cdemodp_lip) goto cdemodpmake
if (%1) == ("cdemodp_lip") goto cdemodpmake
if (%1) == (CDEMODP_lip) goto cdemodpmake
if (%1) == ("CDEMODP_lip") goto cdemodpmake

cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.c /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc
goto end

:cplusplus
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.cpp /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib msvcrt.lib /nod:libc
goto end

:occimakeobj
ott userid=scott/tiger intype=%1.typ outtype=%1out.type code=cpp hfile=%1h.h cppfile=%1o.cpp mapfile=%1m.cpp attraccess=private

cl -GX -DWIN32COMMON -I. -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.cpp %1m.cpp %1o.cpp /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib msvcrt.lib msvcprt.lib oraocci9.lib /nod:libc 
goto end

:occimake
cl -GX -DWIN32COMMON -I. -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.cpp /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib msvcrt.lib msvcprt.lib oraocci9.lib /nod:libc
goto end

:callucb
echo Use ociucb.bat to make user call back demos
goto end


:callucb2
REM build cdemoucb.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.c /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc
REM call batch file to build ociucb.dll from cdemoucbl.c
call ociucb.bat %1l
goto end


:dllmake
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.c /link /Dll /out:%1l.dll /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /export:qxiqtbi /export:qxiqtbd /export:qxiqtbu /export:qxiqtbs /export:qxiqtbf /export:qxiqtbc 
goto end

:dllmake2
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT %1.c /link /Dll /out:%1l.dll /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /export:qxiqtbpi /export:qxiqtbpd /export:qxiqtbpu /export:qxiqtbps /export:qxiqtbpf /export:qxiqtbpc
goto end

:cdemodpmake
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpco.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpco.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpin.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpin.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpit.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpit.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdplp.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdplp.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpno.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpno.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpro.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpro.exe
cl -I%ORACLE_HOME%\oci\include -I. -D_DLL -D_MT cdemdpss.c cdemodp.c  /link /LIBPATH:%ORACLE_HOME%\oci\lib\msvc oci.lib kernel32.lib msvcrt.lib /nod:libc /out:cdemdpss.exe
goto end

:usage
echo.
echo Usage: make filename [i.e. make oci01]
echo.
:end
