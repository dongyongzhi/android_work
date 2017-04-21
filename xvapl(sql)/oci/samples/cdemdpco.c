#ifdef RCSID
static char *RCSid =
   "$Header: cdemdpco.c 04-apr-2001.11:17:46 eegolf Exp $ ";
#endif /* RCSID */

/*
**      Copyright (c)  2001 Oracle Corporation.  All rights reserved.
*/

/* 
** Directions:
** 1. make -f demo_rdbms.mk build_dp EXE=cdemdpco OBJS=cdemdpco.o
**        makes the cdemdpco executable
** 2. sqlplus OE/OE @cdemdp9i.sql 
**        creates the Oracle 9.0 types tbl
** 3. cdemdpco < cdemdpco.dat
**        runs the executable
** 4. to retrieve data, select from "dp_api_demo1" as "OE/OE"
**
*/

/*
   NAME
     cdemdpco.c - An example C program that loads a column object 
                        via Direct Path API.

   DESCRIPTION
      A client module to be used with the cdemodp.c driver.

   NOTES
     This module loads selected types as defined in the Sample Schema.
     Refer to Cdemdp9i.sql for more information on the use of the
     Sample Schema with the DP API demo modules.

   MODIFIED   (MM/DD/YY)
   eegolf       04/04/01 - Merged eegolf_demo_update
   eegolf       03/27/01 - Creation
 */

#include <sys/types.h>
#include <oci.h>
#include <cdemodp.h>


struct col  objx_01_col [] =
{
  {
    (text *)"street_address", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, (struct obj *) 0
  },
  {
    (text *)"postal_code", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, (struct obj *) 0
  },
  {
    (text *)"city", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, (struct obj *) 0
  },
  {
    (text *)"state_province", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, (struct obj *) 0
  },
  {
    (text *)"country_id", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, (struct obj *) 0
  }
};

struct fld  objx_01_fld [] =
{
  { 26, 65, 40, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },     /* street_address */
  { 66, 75, 10, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },     /* postal_code */
  { 76,105, 30, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },     /* city */
  {106,115, 10, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },     /* state_province */
  {116,117,  2, FLD_INLINE|FLD_STRIP_TRAIL_BLANK }      /* country_id */
};

struct obj cust_address_typ =
{
  (text *) "CUST_ADDRESS_TYP", 5, objx_01_col, objx_01_fld, 0, 0, 0, 0, OBJ_OBJ};


struct col column[] =
{
  {
    (text *)"cust_first_name", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0
  },
  {
    (text *)"cust_last_name", 0, SQLT_CHR, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0
  },
  {
    (text *)"cust_address", 0, SQLT_NTY, (text *)0,
    (sword)0, (sword)0, (ub2)0, (ub1)0, & cust_address_typ 
  }
};

/* Field descriptor which maps one-to-one with the column descriptor.
 * For this simple example, fields are strictly positional within
 * an input record.
 */
struct fld field[] =
{
  { 1, 10, 10, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },       /* cust_first_name */
  { 11,20, 10, FLD_INLINE|FLD_STRIP_TRAIL_BLANK },       /* cust_last_name */
  { 21,25,  5, FLD_INLINE|FLD_STRIP_TRAIL_BLANK }        /* cust_address */
};


externdef struct tbl table =
{
  (text *)"OE",                                          /* table owner */
  (text *)"dp_api_demo1",                                 /* table name */
  (text *)""         ,             /* subname (partition, subpartition) */
  (ub2)(sizeof(column) / sizeof(struct col)),      /* number of columns */
  (text *)"DD-MON-YY",                           /* default date format */
  (struct col *)(&column[0]),                     /* column descriptors */
  (struct fld *)(&field[0]),                       /* field descriptors */
  (ub1)0,                                                   /* parallel */
  (ub1)0,                                                      /* nolog */
  (ub4)(64 * 1024)                              /* transfer buffer size */
};

externdef struct sess session =
{
  (text *)"OE",                                              /* user */
  (text *)"OE",                                            /* passwd */
  (text *)"",                                          /* instance name */
  (text *)0,                   /* output file name; NULL implies stderr */
  (ub4)130                                   /* max input record length */
};


/* end of file cdemdpco.c */

