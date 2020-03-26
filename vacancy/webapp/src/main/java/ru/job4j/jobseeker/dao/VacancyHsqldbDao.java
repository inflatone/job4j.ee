package ru.job4j.jobseeker.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import ru.job4j.vacancy.model.VacancyData;

import java.util.List;

// postgres statements with "...ON CONFLICT DO..." are not supported by hsqldb
// https://stackoverflow.com/a/15075997/10375242
public interface VacancyHsqldbDao extends VacancyDao {
    @Override
    @SqlBatch("MERGE INTO vacancy AS v USING (VALUES(:taskId, :title, :description,:url,:dateTime)) AS vals(id, t, d, u, dt) " +
            "        ON (v.title=vals.t AND v.task_id=vals.id)  " +
            "    WHEN MATCHED THEN UPDATE SET v.description=vals.d, v.url=vals.u, v.date=vals.dt" +
            "    WHEN NOT MATCHED THEN INSERT (task_id, title, description, url, date) VALUES vals.id, vals.t, vals.d, vals.u, vals.dt")
    int[] saveAllAndReturnAffectedRows(@BindBean List<VacancyData> vacancies, @Bind("taskId") int taskId);
}
