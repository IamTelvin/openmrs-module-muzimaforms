package org.openmrs.module.muzimaforms.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzimaforms.MuzimaForm;
import org.openmrs.module.muzimaforms.MuzimaFormTag;
import org.openmrs.module.muzimaforms.MuzimaXForm;
import org.openmrs.module.muzimaforms.api.db.hibernate.MuzimaFormDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openmrs.module.muzimaforms.FormBuilder.form;
import static org.openmrs.module.muzimaforms.MuzimaFormBuilder.muzimaform;
import static org.openmrs.module.muzimaforms.MuzimaFormTagBuilder.tag;
import static org.openmrs.module.muzimaforms.XFormBuilder.xForm;

public class MuzimaFormDAOTest extends BaseModuleContextSensitiveTest {

    private MuzimaFormDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = Context.getService(MuzimaFormDAO.class);
        executeDataSet("tagTestData.xml");
        executeDataSet("xformTestData.xml");
    }

    @Test
    public void getAll_shouldGetAll() {
        List<MuzimaForm> list = dao.getAll();
        assertThat(list, hasItem(muzimaform().withId(1)
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .with(xForm().withId(1))
                .with(form().withId(1).withName("Registration Form").withDescription("Form for registration"))
                .instance()));
        assertThat(list, hasItem(muzimaform().withId(2)
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(3).withName("Encounter"))
                .with(tag().withId(4).withName("HIV"))
                .with(xForm().withId(2))
                .with(form().withId(2).withName("PMTCT Form").withDescription("Form for PMTCT"))
                .instance()));
        assertThat(list, hasItem(muzimaform().withId(3)
                .with(form().withId(3).withName("Ante-Natal Form").withDescription("Form for ante-natal care"))
                .instance()));
        assertThat(list.size(), is(3));
    }

    @Test
    public void getXForms_shouldGetXForms() {
        List<MuzimaXForm> all = dao.getXForms();
        assertThat(all.size(), is(1));
    }

    @Test
    public void findById_shouldFindById() {
        MuzimaForm form = dao.findById(1);
        assertThat(form, is(muzimaform().withId(1)
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .with(xForm().withId(1))
                .with(form().withId(1).withName("Registration Form").withDescription("Form for registration"))
                .instance()));
    }

    @Test
    public void saveForm_shouldSaveForm() {
        dao.saveForm(muzimaform().withId(1).with(tag().withId(1).withName("Registration")).instance());
        List<MuzimaForm> list = dao.getAll();
        assertThat(list, hasItem(muzimaform().withId(1).with(tag().withId(1).withName("Registration")).instance()));
    }

    @Test
    public void saveForm_shouldAssignAnExistingTag() throws IOException {
        dao.saveForm(muzimaform().withId(1).with(tag().withId(1).withName("Registration")).instance());
        List<MuzimaForm> list = dao.getAll();
        assertThat(list, hasItem(muzimaform().withId(1).with(tag().withId(1).withName("Registration")).instance()));
    }

    @Test
    public void saveForm_shouldAssignANewTag() throws IOException {
        MuzimaForm form = muzimaform().withId(1).with(tag().withName("New Tag")).with(tag().withName("Another Tag")).instance();
        dao.saveForm(form);
        Set<MuzimaFormTag> formTags = form.getTags();
        assertThat(formTags.size(), is(2));
        MuzimaFormTag newTag = (MuzimaFormTag) formTags.toArray()[0];
        assertThat(newTag.getId(), notNullValue());
        TagService tagService = Context.getService(TagService.class);
        List<MuzimaFormTag> tags = tagService.getAll();
        assertThat(tags, hasItem(newTag));
    }
}
