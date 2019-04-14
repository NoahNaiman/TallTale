import React, {Component} from 'react';

export default class PanelContainer extends Component {
    render = () => {
        return (
            <>
                <div className={`tile is-parent ${this.props.className}`}>
                    { this.props.children }
                </div>
            </>
        );
    };
}
