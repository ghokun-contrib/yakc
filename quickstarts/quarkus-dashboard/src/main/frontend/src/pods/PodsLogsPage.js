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
import {connect} from 'react-redux';
import {AutoSizer, List} from 'react-virtualized';
import Convert from 'ansi-to-html';
import dompurify from 'dompurify';
import metadata from '../metadata';
import p from '../pods';
import Card from '../components/Card';
import DashboardPage from '../components/DashboardPage';
import Dropdown from '../components/Dropdown';
import Link from '../components/Link';

import Switch from '../components/Switch';
import './PodsLogsPage.css';

const ansi = new Convert();

const ContainerDropdown = ({containers, selectedContainer, setSelectedContainer}) => (
  <Dropdown
    className='ml-2'
    text={selectedContainer?.name ?? ''}
    closeOnPanelClick={true}
  >
    {containers.map(c =>
      <Dropdown.Item key={c.name} onClick={() => setSelectedContainer(c)}>{c.name}</Dropdown.Item>
    )}
  </Dropdown>
);



const PodsLogsPage = ({uid, namespace, name, containers}) => {
  const {
    listRef, log, follow, setFollow, selectedContainer, setSelectedContainer
  } = p.useLogs(namespace, name, containers);
  const rowRenderer = ({key, index, style}) => (
    <div key={key} className='whitespace-no-wrap' style={{...style, width: 'auto'}}
         dangerouslySetInnerHTML={{__html: dompurify.sanitize(ansi.toHtml(log[index]))}} />
  );
  return (
    <DashboardPage
      title={`Pods - ${namespace} - ${name} - Logs`}
      className='pods-logs-page'
    >
      <div className='absolute inset-0 md:p-4 flex flex-col'>
        <Card className='flex-1 flex flex-col'>
          <Card.Title className='flex items-center'>
            <div className='flex-1 flex items-center'>
              Logs
              <Link.RouterLink className='ml-2' to={`/pods/${uid}`}>{name}</Link.RouterLink>
              <ContainerDropdown
                containers={containers} setSelectedContainer={setSelectedContainer} selectedContainer={selectedContainer}
              />
            </div>
            <div className='justify-self-end text-sm font-normal'>
              <Switch label='Follow' checked={follow} onChange={() => setFollow(!follow)} />
            </div>
          </Card.Title>
          <Card.Body className='flex-1 bg-black text-white font-mono text-sm'>
            <AutoSizer>
              {({ height, width }) => (
                <List
                  ref={listRef}
                  height={height}
                  width={width}
                  rowCount={log.length}
                  rowHeight={19}
                  rowRenderer={rowRenderer}
                  className='custom-scroll-dark'
                />
              )}
            </AutoSizer>
          </Card.Body>
        </Card>
      </div>
    </DashboardPage>
  );
};

const mapStateToProps = ({pods}) => ({pods});

const mergeProps = ({pods}, dispatchProps, {match: {params: {uid}}}) => ({
  ...dispatchProps,
  uid,
  namespace: metadata.selectors.namespace(pods[uid]),
  name: metadata.selectors.name(pods[uid]),
  containers: p.selectors.containers(pods[uid])
});

export default connect(mapStateToProps, null, mergeProps)(PodsLogsPage);