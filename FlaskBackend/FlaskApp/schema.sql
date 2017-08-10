drop table if exists data; 

create table data (
         id integer primary key autoincrement,
         mean text not null,
         max text not null,
         min text not null,
         data text not null,
         gender text not null,
	 valid integer default 1);
