import React, {Component} from 'react';
import {dropLastWhile, includes, last, replace, split} from "ramda";

const axios = require('axios');

export default class Poller extends Component {
    tick = () => {
        axios.get('http://35.247.110.35:6663/api/test')
            .then(({data}) => {
                const end = last(dropLastWhile(x => x === '', split('\n', data)));
                if (end === 'FINISHED') {
                    this.props.update('100');
                } else if (includes('Progress is', end ? end : '')) {
                    this.props.update(replace('Progress is ', '', end));
                }
            })
    };

    componentDidMount() {
        axios.post('http://35.247.110.35:6663/api/get', {
            text: this.props.text,
        });
        setTimeout(() => {
            this.checker = setInterval(
                () => this.tick(),
                100
            );
        }, 1000);
    }

    render = () => {
        return (
            <>
            </>
        );
    };
}
