/*instrumentation.js*/
// Require dependencies
const { NodeSDK } = require('@opentelemetry/sdk-node');
const { ConsoleSpanExporter } = require('@opentelemetry/sdk-trace-node');
// const {
//   getNodeAutoInstrumentations,
// } = require('@opentelemetry/auto-instrumentations-node');
const { HttpInstrumentation } = require('@opentelemetry/instrumentation-http');
// const {
//   PeriodicExportingMetricReader,
//   ConsoleMetricExporter,
// } = require("@opentelemetry/sdk-metrics");
const api = require('@opentelemetry/api');
const {
  B3Propagator,
  B3InjectEncoding,
} = require('@opentelemetry/propagator-b3');
const { CompositePropagator } = require('@opentelemetry/core');

api.propagation.setGlobalPropagator(
  new CompositePropagator({
    propagators: [
      new B3Propagator(),
      new B3Propagator({ injectEncoding: B3InjectEncoding.MULTI_HEADER }),
    ],
  })
);

const sdk = new NodeSDK({
  traceExporter: new ConsoleSpanExporter(),
  //   metricReader: new PeriodicExportingMetricReader({
  //     exporter: new ConsoleMetricExporter(),
  //   }),
  instrumentations: [new HttpInstrumentation()], // [getNodeAutoInstrumentations()],
});

sdk.start();
