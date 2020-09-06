/*
 * Copyright 2020 Marc Nuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
import React from 'react';
import {
  Grid
} from 'tabler-react';
import events from './events';
import nodes from './nodes';
import DashboardPage from './components/DashboardPage';
import StatusCard from './components/StatusCard';
import icons from './icons';


const Home = () => (
  <DashboardPage>
    <Grid>
      <Grid.Row cards={true}>
        <Grid.Col width={6} sm={4} lg={2}>
          <nodes.NodesCard />
        </Grid.Col>
        <Grid.Col width={6} sm={4} lg={2}>
          <StatusCard
            header='Pods'
            icon={icons.pod}
            ready={3}
            total={4}
            progressWidth={Math.round(3/4*100)}
          />
        </Grid.Col>
      </Grid.Row>
      <Grid.Row>
        <Grid.Col>
          <events.List />
        </Grid.Col>
      </Grid.Row>
    </Grid>
  </DashboardPage>
);

export default Home;
