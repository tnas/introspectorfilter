package io.github.tnas.introspectorfilter;

import edu.neu.ccs.demeter.dj.Visitor;
import io.github.tnas.introspectorfilter.lieberherr.demeterdj.Bus;
import io.github.tnas.introspectorfilter.lieberherr.demeterdj.Person;
import io.github.tnas.introspectorfilter.lieberherr.demeterdj.Village;
import org.apache.commons.lang3.StringUtils;

public class FilterVisitor extends Visitor {

    private final String filter;
    private boolean foundValue;

    public FilterVisitor(Object filter) {
        this.filter = StringUtils.stripAccents(filter.toString().trim().toLowerCase());
        this.foundValue = false;
    }

    @Override
    public void before(Object obj, Class cl) {
        this.foundValue |= switch (obj) {
            case Bus bus -> StringUtils.stripAccents(bus.getName().toLowerCase()).contains(filter);
            case Person person -> StringUtils.stripAccents(person.getName().toLowerCase()).contains(filter);
            case Village village -> StringUtils.stripAccents(village.getName().toLowerCase()).contains(filter);
            default -> false;
        };
    }

    @Override
    public void after(Object obj, Class cl) {
        if (this.foundValue) {
            this.finish();
        }
    }

    @Override
    public void finish() {

    }

    @Override
    public Object getReturnValue() {
        return this.foundValue;
    }
}
