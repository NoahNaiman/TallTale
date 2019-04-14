import React, {Component} from 'react';

export default class LoadingBar extends Component {
    render = () => {
        return (
            <>
                <progress
                    className="progress is-success"
                    value={this.props.value * 100}
                    max="100">
                    { this.props.value * 100 }%
                </progress>
            </>
        );
    };
}
