import React, {Component} from 'react';

export default class Panelizer extends Component {
    render = () => {
        return (
            <>
                <div className="tile is-ancestor">
                    <div className="tile is-vertical">
                        <div className="tile">
                            { this.props.children }
                        </div>
                    </div>
                </div>
            </>
        );
    };
}
