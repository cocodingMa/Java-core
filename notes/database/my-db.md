## 实用sql

记录平时实用的sql

<!-- TOC -->

- [实用sql](#实用sql)
    - [JOIN  条件](#join--条件)
    - [EXCEPT 交集](#except-交集)

<!-- /TOC -->

### JOIN  条件

```
SELECT DISTINCT ta.id,ta.mobile,xe.imei,xe.only_id,xm.number,xm.duration,xm.call_time,xm.type
FROM x_monitor xm
LEFT JOIN x_equipment xe on xe.id = xm.equipment_id
LEFT JOIN t_monitor_token tmt on tmt.only_id = xe.only_id AND tmt.create_time =(SELECT create_time FROM t_monitor_token where create_time < xm.call_time AND only_id =      xe.only_id ORDER BY create_time desc limit 1)
LEFT JOIN t_admin ta ON ta.mobile = tmt.mobile
where 1=1
and (#{imei} is null or xe.imei = #{imei})
and (#{mobile} is null or xm.number = #{mobile})
order by xm.call_time DESC
```

### EXCEPT 交集

```
SELECT
	COUNT (*)
FROM
	(SELECT
			loan_app_id
		FROM
			t_loan_app_status_log
		WHERE
			create_time < #{ startTime }
		AND new_status = #{ status }
		EXCEPT
			SELECT
				loan_app_id
			FROM
				t_loan_app_status_log
			WHERE
				create_time < #{ startTime }
			AND old_status =#{ status }
	) A;
```