alter table d_siocw.soc_comprobante add(esq_cvetipocomprob varchar(5) before usr_codigo);
alter table d_siocw.soc_comprobante add(esq_codesqcont varchar(20) before usr_codigo);
alter table d_siocw.swf_mensaje add (men_nrolavado integer before men_auditusr);

create   index rengcomay on d_siocw.soc_rengscomp (sol_coddestorig,cla_debehaber ASC);

-- grants contbcb
grant select on "d_conta".esquema to "d_siocw" as "d_conta";
grant select on "d_conta".reng_esq to "d_siocw" as "d_conta";
grant select on "d_conta".cuenta_mayor to "d_siocw" as "d_conta";
grant select on "d_conta".concepto_mayor to "d_siocw" as "d_conta";

