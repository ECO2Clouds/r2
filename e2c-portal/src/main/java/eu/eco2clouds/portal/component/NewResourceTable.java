/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.portal.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

/**
 *
 *  
 */
public class NewResourceTable extends Table {

    BeanItemContainer<NewResourceTableBean> container = new BeanItemContainer<NewResourceTableBean>(NewResourceTableBean.class);

    public NewResourceTable() {
        super("Resource list");
        this.setImmediate(true);
        this.setSizeFull();
        this.setSelectable(true);
        this.setMultiSelect(false);

        this.render();
    }
    
    private void render() {
        this.setContainerDataSource(container);
        this.setVisibleColumns(new Object[]{"name", "location", "type"});

    }
    
    public void addItem(NewResourceTableBean rb) {
        this.container.addBean(rb);
        this.setContainerDataSource(container);
    }

}
