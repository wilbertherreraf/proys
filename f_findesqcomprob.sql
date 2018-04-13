drop function f_findesqcomprob;

--/
create function f_findesqcomprob(p_cpb_codigo like soc_comprobante.cpb_codigo) returning varchar(20)
define v_cpb_codigo like soc_comprobante.cpb_codigo;
define v_cod_esquemaresp, v_cod_esquema like contbcb:reng_esq.cod_esquema;
define v_cod_mayor like contbcb:reng_esq.cod_mayor;
define v_cve_debe_haber like contbcb:reng_esq.cve_debe_haber;

define contar, v_cont_mayor, v_es_esquema, ult_count, v_cont, v_cont_esq, nro_posi, v_diferencia integer;

--003557 003606 ift COBRTO DE UTILES
let v_cont_esq = 0;
            let nro_posi = 1;
            let ult_count = "";
            let contar = 0;
let v_cod_esquemaresp = null;
        -- obtenmos los esquemas candidatos                    
    foreach
        select distinct cod_esquema
        into v_cod_esquema
        from
        (select re.cod_esquema,
            (select count(*)
                        from contbcb:reng_esq re0
                        where re0.cod_esquema = re.cod_esquema
                        and not exists (
                            select *
                            from soc_rengscomp
                            where cpb_codigo = p_cpb_codigo
                            and sol_coddestorig = re0.cod_mayor
                            and cla_debehaber = re0.cve_debe_haber
                    )) inexistentes
        from contbcb:reng_esq re
        where re.cod_esquema in ('003452','004031','003607','003606','003556','002256','004333','004025',
        '003684','000874','003168','003259','003216','003557','003215','004030',
        '004330','000561','003940','004332','003288','004331','003956','003213',
        '003307','003446','001242','000769','004033','004328','004329','004327')
        and exists (select 1
                    from soc_rengscomp rc
                    where rc.cpb_codigo = p_cpb_codigo
                    and rc.cla_debehaber = re.cve_debe_haber
                    and rc.sol_coddestorig = re.cod_mayor)
        ) esque
        where inexistentes = 0

            -- para cada esquema candidato verificamos que esten todos los registros en el comprobante
            select count(*) --distinct (regsreng - regs) diff
            into v_diferencia
            from (
                select *
                from 
                    (select distinct cla_debehaber,sol_coddestorig cod_mayor
{            (select count(*) from (select distinct cla_debehaber,sol_coddestorig cod_mayor
                    from soc_rengscomp
                    where cpb_codigo = p_cpb_codigo))  regsreng }
                    from soc_rengscomp
                    where cpb_codigo = p_cpb_codigo) rm,
                outer
                (select re.cod_esquema, re.cve_debe_haber, re.cod_mayor
{
                    (select count(*)
                    from contbcb:reng_esq re
                    where re.cod_esquema = v_cod_esquema) regs
}
                from contbcb:reng_esq re
                where re.cod_esquema = v_cod_esquema) t
                where rm.cla_debehaber = t.cve_debe_haber
                and rm.cod_mayor = t.cod_mayor
            ) tr
            where cod_esquema is null;
            
            if (v_diferencia = 0) then
            -- esquema encontrado
                let v_cod_esquemaresp = v_cod_esquema;
            end if
            if (v_diferencia = 1) then
                if v_cod_esquemaresp is null then
                    let v_cod_esquemaresp = v_cod_esquema;
                end if
            end if
    end foreach;
    return v_cod_esquemaresp;
end function;
/--

